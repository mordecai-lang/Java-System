package com.hostel.handler;

import com.sun.net.httpserver.HttpExchange;

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
        System.out.println("handleLogin was called");
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = "Accepting only POST";
            ResponseUtil.send(exchange, 405, response);
            return;
        }
       
        InputStream input = exchange.getRequestBody();
       
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> data = FormDataParser.parse(body);

        String email = data.get("email");
        String password = data.get("password");

        System.out.println("About to check login");

        boolean success = AuthenticationService.login(email, password);
        System.out.println("Login check completed: " + success);
        
        String response;
        int statusCode;
        if (success) {
            if (AuthenticationService.getCurrentRole().equals("ADMIN")) {
                response = "ADMIN";
            } else {
                response = "USER";
            }
            statusCode = 200;

        } else {
            response = "Invalid email or password!";
            statusCode = 401;
        }

        System.out.println("Response: " + response);
        System.out.println("Status code: " + statusCode);
        ResponseUtil.send(exchange, statusCode, response);
    }
}