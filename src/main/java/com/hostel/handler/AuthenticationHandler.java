package com.hostel.handler;
import com.hostel.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.hostel.LoginResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.hostel.util.FormDataParser;
import com.hostel.util.ResponseUtil;
import com.hostel.service.AuthenticationService;

public class AuthenticationHandler {
    
    public static void handleRegister(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = "Accepting only POST";
            ResponseUtil.send(exchange, 405, response);
            return;
        }

        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);

        Map<String, String> data = FormDataParser.parse(body);
        String name = data.get("fullName");
        String email = data.get("email");
        String password = data.get("password");

        boolean success = AuthenticationService.register(name, email, password);
        String response;
        int statusCode;
        if (success) {
            response = "Registration successful!";
            statusCode = 201;
        } else {
            response = "Invalid details or email already registered!";
            statusCode = 409;
        }
        ResponseUtil.send(exchange, statusCode, response);
    }

    public static void handleLogin(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            ResponseUtil.send(exchange, 405, "Accepting only POST");
            return;
        }

        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> data = FormDataParser.parse(body);

        String email = data.get("email");
        String password = data.get("password");

        LoginResult loginResult = AuthenticationService.login(email, password);

        if (loginResult == null) {
            ResponseUtil.send(exchange, 401, "Invalid email or password!");
            return;
        }

        // Send this user's session ID to the browser.
        SessionManager.sendSessionCookie(exchange, loginResult.getSessionId());

        String response = loginResult.getRole().equals("ADMIN")
            ? "ADMIN"
            : "USER";

        ResponseUtil.send(exchange, 200, response);
    }

}