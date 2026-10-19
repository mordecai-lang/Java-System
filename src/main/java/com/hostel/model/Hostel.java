package com.hostel.model;

public class Hostel{
  private int id;
  private String name;
  private String location;
  private String description;

  public Hostel(int id, String name, String location, String description) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.description = description;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public String getLocation() {
    return location;
  }

  public String getDescription() {
    return description;
  }
}