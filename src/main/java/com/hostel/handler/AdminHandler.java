package com.hostel.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.hostel.util.FormDataParser;
import com.hostel.util.ResponseUtil;
import com.hostel.service.AdminService;

import com.hostel.model.User;
import com.hostel.service.AuthenticationService;
import com.hostel.util.JsonResponseUtil;
import com.hostel.util.JsonUtil;

import java.util.List;

public class AdminHandler{
  public static void handleAddUsers(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      String response = "Accepting only POST requests";
      ResponseUtil.send(exchange, 405, response);
      return;
    }  
    
    InputStream input = exchange.getRequestBody();
    
    String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);

    Map<String, String> data = FormDataParser.parse(body);

    String name = data.get("fullName");
    String email = data.get("email");
    String password = data.get("password");
    String role = data.get("role");

    boolean success = AdminService.adduser(name,email, password, role);

    String response;
    int statusCode;
    if (success) {
      response = "User added successfully by an admin!";
      statusCode = 201;
    } else {
      response = "Invalid details or email already registered!";
      statusCode = 409;
    }
    ResponseUtil.send(exchange, statusCode, response);
  }    

  public static void handleRemoveUsers(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      String response = "Accepting only POST requests";
      ResponseUtil.send(exchange, 405, response);
      return;
    }  
    
    InputStream input = exchange.getRequestBody();
    
    String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
    Map<String, String> data = FormDataParser.parse(body);

    String email = data.get("removeUserEmail");
    System.out.println("Email received for deletion: [" + email + "]");
    boolean success = AdminService.removeuser(email);

    String response;
    int statusCode;
    if (success) {
      response = "User removed successfully by an admin!";
      statusCode = 200;
    } else {
      response = "Invalid details or email not found!";
      statusCode = 404;
    }
    ResponseUtil.send(exchange, statusCode, response);
  }
  
  // Viewing Users
  public static void handleUsers(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Accepting only GET");
        return;
    }
    List<User> users = AuthenticationService.getLatestUsers();

    String json = JsonUtil.usersToJson(users);
    JsonResponseUtil.send(exchange, 200, json);

  }

  public static void handleHostels(HttpExchange exchange) throws IOException {
    
    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        ResponseUtil.send(exchange, 405, "Accepting only POST");
        return;
    }

    InputStream input = exchange.getRequestBody();
    String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
    
    Map<String, String> data = FormDataParser.parse(body);

    String hostelName = data.get("hostelName");
    String location = data.get("location");
    String description = data.get("description");
   
    boolean success = AdminService.addHostel(hostelName, location, description);

    String response;
    int statusCode;
    if (success) {
        response = "Hostel added successfully by an admin!";
        statusCode = 201;
    } else {
        response = "Invalid details or hostel already exists!";
        statusCode = 409;
    }
    ResponseUtil.send(exchange, statusCode, response);
  }

  //Viewing Hostels
  public static void handleViewHostels(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Accepting only GET");
        return;
    }
    List<Hostel> hostels = AdminService.getLatestHostels();

    String json = JsonUtil.hostelsToJson(hostels);
    JsonResponseUtil.send(exchange, 200, json);
  }
}