package com.hostel.model;
import java.math.BigDecimal;
public class Hostel{
  private int id;
  private String name;
  private String location;
  private String description;
  private int applicationCapacity;
  private java.math.BigDecimal accommodationFee;

  public Hostel(int id, String name, String location, String description, int applicationCapacity, java.math.BigDecimal accommodationFee) {  
    this.id = id;
    this.name = name;
    this.location = location;
    this.description = description;
    this.applicationCapacity = applicationCapacity; // Default value if not provided
    this.accommodationFee = accommodationFee;
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

  public int getApplicationCapacity() {
    return applicationCapacity;
  }

  public java.math.BigDecimal getAccommodationFee() {
    return accommodationFee;
  }

}