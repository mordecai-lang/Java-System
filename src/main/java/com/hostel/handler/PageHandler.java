
package com.hostel.handler;

import com.hostel.SessionManager;
import com.hostel.SessionManager.UserSession;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PageHandler {

    public static void handlePages(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String fileName;

        UserSession session = SessionManager.getSession(exchange);
        boolean authenticated = session != null;
        boolean isAdmin = authenticated
                && "ADMIN".equals(session.getRole());

        if (path.equals("/")) {
            fileName = "index.html";

        } else if (path.equals("/register")) {
            fileName = "register.html";

        } else if (path.equals("/home")) {
            fileName = authenticated ? "home.html" : "index.html";

        } else if (path.equals("/book-rooms")) {
            fileName = authenticated ? "book-rooms.html" : "index.html";

        } else if (path.equals("/admin")) {
            fileName = isAdmin ? "admin.html" : "index.html";

        } else if (path.equals("/users")) {
            fileName = isAdmin ? "users.html" : "index.html";

        } else if (path.equals("/rooms")) {
            fileName = isAdmin ? "rooms.html" : "index.html";

        } else if (path.equals("/hostels")) {
            fileName = isAdmin ? "hostels.html" : "index.html";

        } else if (path.equals("/manage-booking")) {
            fileName = isAdmin ? "manage-booking.html" : "index.html";

        } else {
            fileName = path.substring(1);
        }

        try (InputStream input =
                     PageHandler.class.getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (input == null) {
                byte[] response = "404 Not Found".getBytes();

                exchange.sendResponseHeaders(404, response.length);

                try (OutputStream output = exchange.getResponseBody()) {
                    output.write(response);
                }

                return;
            }

            byte[] file = input.readAllBytes();

            if (fileName.endsWith(".html")) {
                exchange.getResponseHeaders().set(
                        "Content-Type", "text/html; charset=UTF-8");

            } else if (fileName.endsWith(".css")) {
                exchange.getResponseHeaders().set(
                        "Content-Type", "text/css; charset=UTF-8");

            } else if (fileName.endsWith(".js")) {
                exchange.getResponseHeaders().set(
                        "Content-Type", "application/javascript; charset=UTF-8");
            }

            exchange.sendResponseHeaders(200, file.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(file);
            }
        }
    }
}
