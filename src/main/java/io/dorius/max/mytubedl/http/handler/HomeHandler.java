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

import io.dorius.max.mytubedl.util.FileUtil;

import java.io.IOException;

public class HomeHandler extends ApiHandler {

    @Override
    protected void handle(Exchange ex) throws IOException {
        String data = FileUtil.load("classpath:/templates/home.html");
        ex.writeBodyAsUtf8(data);
    }

}
