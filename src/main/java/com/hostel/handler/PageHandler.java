package com.hostel.handler;

import com.hostel.service.AuthenticationService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PageHandler {

    public static void handlePages(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();
        String fileName;

        if (path.equals("/")) {
            fileName = "index.html";

        } else if (path.equals("/register")) {
            fileName = "register.html";

        } else if (path.equals("/home")) {

            if (AuthenticationService.isAuthenticated()) {
                fileName = "home.html";
            } else {
                fileName = "index.html";
            }

        } else if (path.equals("/admin")) {

            if (AuthenticationService.isAuthenticated()
                    && AuthenticationService.getCurrentRole().equals("ADMIN")) {

                fileName = "admin.html";

            } else {
                fileName = "index.html";
            }
        } else if (path.equals("/users")) {
            if (AuthenticationService.isAuthenticated() && AuthenticationService.getCurrentRole().equals("ADMIN")) {
                fileName = "users.html";
            } else {
                fileName = "index.html";
            }
        }else if (path.equals("/rooms")) {
            if (AuthenticationService.isAuthenticated() && AuthenticationService.getCurrentRole().equals("ADMIN")) {
                fileName = "rooms.html";
            } else {
                    fileName = "index.html";
            }
        }else if (path.equals("/hostels")) {
            if (AuthenticationService.isAuthenticated() && AuthenticationService.getCurrentRole().equals("ADMIN")) {
                fileName = "hostels.html";
            } else {
                fileName = "index.html";
            }
        } else {
            fileName = path.substring(1);
            }
        

        InputStream input = PageHandler.class.getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404,response.getBytes().length);

            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.close();

            return;
        }

        byte[] file = input.readAllBytes();

        if (fileName.endsWith(".html")) {
            exchange.getResponseHeaders().set("Content-Type", "text/html");

        } else if (fileName.endsWith(".css")) {
            exchange.getResponseHeaders().set("Content-Type", "text/css");

        } else if (fileName.endsWith(".js")) {
            exchange.getResponseHeaders().set("Content-Type", "application/javascript");
        }
        exchange.sendResponseHeaders(200, file.length);
        OutputStream output = exchange.getResponseBody();
        output.write(file);
        output.close();
    }
}