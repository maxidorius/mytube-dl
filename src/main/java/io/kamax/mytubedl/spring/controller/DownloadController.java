/*
 * mytube-dl - WebUI for youtube-dl
 * Copyright (C) 2017 Maxime Dor
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

package io.kamax.mytubedl.spring.controller;

import io.kamax.mytubedl.MyTubeDl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class DownloadController {

    @Autowired
    private MyTubeDl mtdl;

    @RequestMapping(method = POST, path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(HttpServletResponse res, @RequestParam String url) throws IOException, InterruptedException, TimeoutException {
        URL target = new URL(url);
        String filename = mtdl.download(target);
        File file = new File(filename);

        res.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
        res.setContentLengthLong(FileUtils.sizeOf(file));
        IOUtils.copy(new FileInputStream(filename), res.getOutputStream());
        res.flushBuffer();

        FileUtils.deleteQuietly(file);
    }

}
