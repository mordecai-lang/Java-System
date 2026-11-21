package com.hostel.model;
import java.math.BigDecimal;
public class Hostel{
  private int id;
  private String name;
  private String location;
  private String description;
  private int applicationCapacity;
  private java.math.BigDecimal accommodationFee;
  private String roomArrangement;
  private int availableApplications; 

  
  //constructor for user endpoint to get hostels with their remaining application spaces.
  public Hostel(int id, String name, String location, String description, int applicationCapacity, java.math.BigDecimal accommodationFee, String roomArrangement,int availableApplications) {  
    this.id = id;
    this.name = name;
    this.location = location;
    this.description = description;
    this.applicationCapacity = applicationCapacity; // Default value if not provided
    this.accommodationFee = accommodationFee;
    this.roomArrangement = roomArrangement;
    this.availableApplications = availableApplications; // Default value if not provided
  }

//constructor for admin endpoint to get hostels with their remaining application spaces.
  public Hostel(
    int id,
    String name,
    String location,
    String description,
    int applicationCapacity,
    BigDecimal accommodationFee,
    String roomArrangement
  ) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.description = description;
    this.applicationCapacity = applicationCapacity;
    this.accommodationFee = accommodationFee;
    this.roomArrangement = roomArrangement;
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
  public String getRoomArrangement() {
    return roomArrangement;
  }

  public int getAvailableApplications() {
    return availableApplications;
  } 
}