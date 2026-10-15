package com.hostel.handler;

import com.hostel.handler.AuthenticationHandler;
import com.hostel.handler.PageHandler;
import com.hostel.handler.AdminHandler;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);

        server.createContext("/",PageHandler::handlePages);
        server.createContext("/loginData",AuthenticationHandler::handleLogin);
        server.createContext("/registerData",AuthenticationHandler::handleRegister);
        server.createContext("/adminUserData",AdminHandler::handleAddUsers);
        server.createContext("/adminRemoveUser",AdminHandler::handleRemoveUsers);
        server.createContext("/adminGetUsers",AdminHandler::handleUsers);
        server.createContext("/adminAddHostels",AdminHandler::handleHostels);
        server.createContext("/adminViewHostels",AdminHandler::handleViewHostels);
        
        server.start();

        System.out.println("Server running at http://localhost:8080");
    }
}