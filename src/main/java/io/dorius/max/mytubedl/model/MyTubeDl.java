/*
 * mytube-dl - WebUI for youtube-dl
 * Copyright (C) 2017 Max Dor
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

import java.nio.file.Path;
import java.nio.file.Paths;

public class MyTubeDl {

    private static final Logger log = LoggerFactory.getLogger(MyTubeDl.class);

    private YoutubeDlExec exec;

    public MyTubeDl() {
        this(new YoutubeDlExec());
    }

    public MyTubeDl(YoutubeDlExec exec) {
        this.exec = exec;
    }

    public void start() {
        exec.update();
    }

    public void stop() {

    }

    public Path download(String target, DownloadOptions options) {
        return Paths.get(exec.download(target, options));
    }

}
