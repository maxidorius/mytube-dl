/*
 * mytube-dl - WebUI for youtube-dl
 * Copyright (C) 2019 Max Dor
 *
 * https://max.dorius.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.dorius.max.mytubedl.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeDlExec implements YoutubeDl {

    private static final Logger log = LoggerFactory.getLogger(YoutubeDlExec.class);

    private final Pattern dlFilePattern = Pattern.compile("\\[ffmpeg] Destination: (.+)");

    public boolean update() {
        try {
            ProcessResult result = new ProcessExecutor()
                    .command("youtube-dl", "-U")
                    .redirectErrorStream(true).redirectOutput(new LogOutputStream() {

                        @Override
                        protected void processLine(String line) {
                            log.info("youtube-dl: {}", line);
                        }

                    })
                    .execute();

            return result.getExitValue() == 0;
        } catch (IOException | InterruptedException | TimeoutException | InvalidExitValueException e) {
            throw new RuntimeException(e);
        }
    }

    public String download(String target, DownloadOptions options) {
        try {
            log.info("Downloading {}", target);

            log.info("youtube-dl: start");

            List<String> lines = new ArrayList<>();
            ProcessResult result = new ProcessExecutor()
                    .command("youtube-dl", "--no-playlist", "-x", "-f", options.getFormat(), "--audio-format", options.getAudioFormat(), target)
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
        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
