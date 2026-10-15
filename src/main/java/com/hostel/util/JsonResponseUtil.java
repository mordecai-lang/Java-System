package com.hostel.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JsonResponseUtil {

    public static void send(
            HttpExchange exchange,
            int statusCode,
            String json) throws IOException {

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}