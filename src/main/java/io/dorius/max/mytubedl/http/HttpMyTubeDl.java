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

package io.dorius.max.mytubedl.http;

import io.dorius.max.mytubedl.http.handler.DownloadHandler;
import io.dorius.max.mytubedl.http.handler.HomeHandler;
import io.dorius.max.mytubedl.model.MyTubeDl;
import io.undertow.Handlers;
import io.undertow.Undertow;

public class HttpMyTubeDl {

    private MyTubeDl m;

    // I/O
    private Undertow u;

    static {
        // Used in XNIO package, dependency of Undertow
        // We switch to slf4j
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    public HttpMyTubeDl() {
        this(new MyTubeDl());
    }

    public HttpMyTubeDl(MyTubeDl m) {
        this.m = m;

        this.u = Undertow.builder().addHttpListener(8080, "0.0.0.0", Handlers.routing()
                .get("/", new HomeHandler())
                .post("/download", new DownloadHandler(m))
        ).build();
    }

    public void start() {
        m.start();
        u.start();
    }

    public void stop() {
        u.stop();
        m.stop();
    }

}
