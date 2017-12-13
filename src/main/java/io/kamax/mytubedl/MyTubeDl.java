/*
 * mytube-dl - WebUI for youtube-dl
 * Copyright (C) 2017 Maxime Dor
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.mytubedl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MyTubeDl {

    private final Logger log = LoggerFactory.getLogger(MyTubeDl.class);

    private Pattern dlFilePattern = Pattern.compile("\\[ffmpeg\\] Destination: (.+)");

    public String download(URL target) throws IOException, TimeoutException, InterruptedException {
        log.info("Downloading {}", target);

        log.info("youtube-dl: start");

        List<String> lines = new ArrayList<>();
        ProcessResult result = new ProcessExecutor()
                .command("youtube-dl", "--no-playlist", "-x", "--audio-format", "vorbis", "--audio-quality", "0", target.toString())
                .redirectErrorStream(true).redirectOutput(new LogOutputStream() {

                    @Override
                    protected void processLine(String line) {
                        log.info("youtube-dl: {}", line);
                        lines.add(line);
                    }

                }).execute();
        log.info("youtube-dl: end");

        if (result.getExitValue() > 0) {
            throw new RuntimeException(result.getOutput().getUTF8());
        }

        for (String line : lines) {
            Matcher m = dlFilePattern.matcher(line);
            if (m.find()) {
                String filename = m.group(1);
                log.info("File downloaded to {}", filename);
                return filename;
            }
        }

        if (lines.isEmpty()) {
            throw new RuntimeException("No output for youtube-dl, cannot process");
        }

        throw new RuntimeException("Could not extract filename");
    }

}
