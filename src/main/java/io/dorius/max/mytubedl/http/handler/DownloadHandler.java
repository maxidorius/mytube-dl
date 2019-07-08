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

package io.dorius.max.mytubedl.http.handler;

import io.dorius.max.mytubedl.exception.InvalidArgumentException;
import io.dorius.max.mytubedl.model.DownloadOptions;
import io.dorius.max.mytubedl.model.MyTubeDl;
import io.undertow.util.HttpString;
import io.undertow.util.QueryParameterUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Deque;
import java.util.Map;

public class DownloadHandler extends ApiHandler {

    private final MyTubeDl m;

    public DownloadHandler(MyTubeDl m) {
        this.m = m;
    }

    @Override
    protected void handle(Exchange ex) throws IOException {
        String url;

        String reqContentType = ex.getContentType().orElse("application/octet-stream");
        if (StringUtils.equals("application/x-www-form-urlencoded", reqContentType)) {
            String body = ex.getBodyUtf8();
            Map<String, Deque<String>> parms = QueryParameterUtils.parseQueryString(body, StandardCharsets.UTF_8.name());
            url = ex.getQueryParameter(parms, "url");
        } else {
            throw new InvalidArgumentException("Unsupported Content-Type: " + reqContentType);
        }

        Path file = m.download(url, DownloadOptions.getDefault());
        ex.getUnderlying().getResponseHeaders().add(HttpString.tryFromString("Content-disposition"), "attachment; filename=\"" + file.getFileName().toString() + "\"");
        ex.getUnderlying().setResponseContentLength(Files.size(file));
        try (InputStream is = Files.newInputStream(file, StandardOpenOption.READ)) {
            IOUtils.copyLarge(is, ex.getUnderlying().getOutputStream());
        }

        FileUtils.deleteQuietly(file.toFile());
    }

}
