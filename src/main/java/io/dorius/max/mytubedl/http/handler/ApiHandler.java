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

import io.dorius.max.mytubedl.exception.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Instant;

public abstract class ApiHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiHandler.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.startBlocking();

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
        } else {
            handleBlockingRequest(exchange);
        }
    }

    protected void handleBlockingRequest(HttpServerExchange exchange) {
        Exchange ex = new Exchange(exchange);
        try {
            exchange.getResponseHeaders().put(HttpString.tryFromString("Access-Control-Allow-Origin"), "*");
            exchange.getResponseHeaders().put(HttpString.tryFromString("Access-Control-Allow-Methods"), "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().put(HttpString.tryFromString("Access-Control-Allow-Headers"), "Origin, X-Requested-With, Content-Type, Accept, Authorization");

            handle(ex);
        } catch (InvalidArgumentException e) {
            ex.respond(400, "E_INVALID_PARAM", e.getMessage());
            log.debug("Trigger:", e);
        } catch (UnauthorizedException e) {
            ex.respond(401, "E_MISSING_TOKEN", e.getMessage());
            log.debug("Trigger:", e);
        } catch (ForbiddenException e) {
            ex.respond(403, "E_FORBIDDEN", e.getMessage());
            log.debug("Trigger:", e);
        } catch (NotFoundException e) {
            ex.respond(404, "E_NOT_FOUND", e.getMessage());
            log.debug("Trigger:", e);
        } catch (NotImplementedException e) {
            ex.respond(501, "E_NOT_IMPLEMENTED", e.getMessage());
            log.debug("Trigger:", e);
        } catch (Exception e) {
            log.error("Unknown error when handling {} - CHECK THE SURROUNDING LOG LINES TO KNOW THE ACTUAL CAUSE!", exchange.getRequestURL(), e);
            ex.respond(500, ex.buildErrorBody("M_UNKNOWN",
                    StringUtils.defaultIfBlank(
                            e.getMessage(),
                            "An internal server error occurred. Contact your system administrator with Log Reference " +
                                    Instant.now().toEpochMilli()
                    )
            ));
        } finally {
            exchange.endExchange();
        }

        // TODO refactor the common code from the various API handlers into a single class
        if (log.isInfoEnabled()) {
            String protocol = exchange.getConnection().getTransportProtocol().toUpperCase();
            String vhost = exchange.getHostName();
            String remotePeer = exchange.getConnection().getPeerAddress(InetSocketAddress.class).getAddress().getHostAddress();
            String method = exchange.getRequestMethod().toString();
            String path = exchange.getRequestURI();
            int statusCode = exchange.getStatusCode();
            long writtenByes = exchange.getResponseBytesSent();

            if (StringUtils.isEmpty(ex.getError())) {
                log.info("Request - {} - {} - {} - {} {} - {} - {}", protocol, vhost, remotePeer, method, path, statusCode, writtenByes);
            } else {
                log.info("Request - {} - {} - {} - {} {} - {} - {} - {}", protocol, vhost, remotePeer, method, path, statusCode, writtenByes, ex.getError());
            }
        }
    }

    protected abstract void handle(Exchange ex) throws Exception;

}
