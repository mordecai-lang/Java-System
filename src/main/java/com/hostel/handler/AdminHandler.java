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
import com.hostel.model.Hostel;
import com.hostel.model.Room;

import com.hostel.service.AuthenticationService;
import com.hostel.util.JsonResponseUtil;
import com.hostel.util.JsonUtil;

import java.util.List;
import java.math.BigDecimal;

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

 // Adding rooms
  public static void handleAddRooms(HttpExchange exchange)throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        ResponseUtil.send(exchange, 405, "Accepting only POST");
        return;
    }
    
    //1.Read the POST request body sent by JavaScript
    String body = new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);

    //2.Parse the submitted form data
    Map<String, String> data = FormDataParser.parse(body);

    //3.Extract values matching your existing HTML IDs
    String hostelIdStr = data.get("hostelId");
    String roomNumber = data.get("roomNumber");
    String totalBedsStr = data.get("beds");
    String roomType = data.get("roomType");
    String feeStr = data.get("accommodationFee");
    String status = data.get("roomStatus");

    //4.Validate required fields
    if (hostelIdStr == null || roomNumber == null
            || totalBedsStr == null || roomType == null
            || feeStr == null
            || hostelIdStr.isBlank() || roomNumber.isBlank()
            || totalBedsStr.isBlank() || roomType.isBlank()
            || feeStr.isBlank()) {
        
        ResponseUtil.send(exchange, 400, "Please provide all required fields.");
        return;
    }

    // The status dropdown defaults to AVAILABLE in your HTML.
    if (status == null || status.isBlank()) {
        status = "AVAILABLE";
    }

    try {
        int hostelId = Integer.parseInt(hostelIdStr);
        int totalBeds = Integer.parseInt(totalBedsStr);

        // BigDecimal is more appropriate for monetary values.
        java.math.BigDecimal accommodationFee =
            new java.math.BigDecimal(feeStr);

        if (hostelId <= 0 || totalBeds < 1
                || accommodationFee.signum() < 0) {

            ResponseUtil.send(exchange, 400, "Invalid numeric values.");
            return;
        }

        if (!java.util.Set.of("Single", "Double", "Shared")
                .contains(roomType)) {
            ResponseUtil.send(exchange, 400, "Invalid room type.");
            return;
        }

        if (!java.util.Set.of("AVAILABLE", "MAINTENANCE", "CLOSED")
            .contains(status)) {
            ResponseUtil.send(exchange, 400, "Invalid room status.");
            return;
        }


        // 5. Call the service to save the room
        boolean success = AdminService.addRoom(hostelId,roomNumber.trim(),totalBeds,roomType,accommodationFee,status);

        if (success) {
            ResponseUtil.send(exchange, 201, "Room added successfully!");
        }else {
            ResponseUtil.send(exchange, 409,"Room could not be added. Check the hostel and room details.");
        }

    } catch (NumberFormatException e) {
        ResponseUtil.send(exchange, 400, "Invalid hostel ID or bed count.");

    } catch (IllegalArgumentException e) {
        ResponseUtil.send(exchange, 400, "Invalid accommodation fee.");

    } catch (Exception e) {
        e.printStackTrace();
        ResponseUtil.send(exchange, 500, "An error occurred while adding the room.");
    }
  }

  // Viewing Rooms
  public static void handleViewRooms(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Accepting only GET");
        return;
    }
    List<Room> rooms = AdminService.getLatestRooms();

    String json = JsonUtil.roomsToJson(rooms);
    JsonResponseUtil.send(exchange, 200, json);
  }
}