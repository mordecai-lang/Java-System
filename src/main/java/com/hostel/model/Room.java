
package com.hostel.model;

import java.math.BigDecimal;

public class Room {
    private int id;
    private int hostelId;
    private String hostelName;
    private String roomNumber;
    private int totalBeds;
    private String roomType;
    private BigDecimal accommodationFee;
    private String status;

    public Room(int id,int hostelId,String roomNumber,int totalBeds,String roomType,BigDecimal accommodationFee,String status,String hostelName) {
        this.id = id;
        this.hostelId = hostelId;
        this.roomNumber = roomNumber;
        this.totalBeds = totalBeds;
        this.roomType = roomType;
        this.accommodationFee = accommodationFee;
        this.status = status;
        this.hostelName = hostelName;
    }

    public int getId() {
        return id;
    }

    public int getHostelId() {
        return hostelId;
    }

    public String getHostelName() {
        return hostelName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public String getRoomType() {
        return roomType;
    }

    public BigDecimal getAccommodationFee() {
        return accommodationFee;
    }

    public String getStatus() {
        return status;
    }
}
