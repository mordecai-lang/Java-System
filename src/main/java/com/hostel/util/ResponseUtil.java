package com.hostel.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    public static void send( HttpExchange exchange, int statusCode, String response) throws IOException {
        
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
          output.write(bytes);
        }
    }
}