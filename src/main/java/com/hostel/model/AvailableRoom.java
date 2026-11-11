
package com.hostel.model;

import java.math.BigDecimal;

public class AvailableRoom {

    private int id;
    private int hostelId;
    private String roomNumber;
    private int totalBeds;
    private String roomType;
    private BigDecimal accommodationFee;
    private String status;
    private String hostelName;
    private int reservedBeds;
    private int remainingBeds;

    public AvailableRoom(
            int id,
            int hostelId,
            String roomNumber,
            int totalBeds,
            String roomType,
            BigDecimal accommodationFee,
            String status,
            String hostelName,
            int reservedBeds,
            int remainingBeds) {

        this.id = id;
        this.hostelId = hostelId;
        this.roomNumber = roomNumber;
        this.totalBeds = totalBeds;
        this.roomType = roomType;
        this.accommodationFee = accommodationFee;
        this.status = status;
        this.hostelName = hostelName;
        this.reservedBeds = reservedBeds;
        this.remainingBeds = remainingBeds;
    }

    public int getId() {
        return id;
    }

    public int getHostelId() {
        return hostelId;
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

    public String getHostelName() {
        return hostelName;
    }

    public int getReservedBeds() {
        return reservedBeds;
    }

    public int getRemainingBeds() {
        return remainingBeds;
    }
}
