package com.hostel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booking {
    private int id;
    private int roomId;
    private String bookedUnder;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int stayMonths;
    private int occupants;
    private BigDecimal accommodationFee;
    private String bookingStatus;
    private String paymentStatus;
    private String email;
    private String phone;

    // Display fields obtained by joining rooms and hostels
    private String roomNumber;
    private String hostelName;

    public Booking(int id,int roomId,String bookedUnder,LocalDate checkInDate,LocalDate checkOutDate,int stayMonths,int occupants,BigDecimal accommodationFee,String bookingStatus,String paymentStatus,String email,String phone,String roomNumber,String hostelName) {
        this.id = id;
        this.roomId = roomId;
        this.bookedUnder = bookedUnder;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.stayMonths = stayMonths;
        this.occupants = occupants;
        this.accommodationFee = accommodationFee;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.email = email;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.hostelName = hostelName;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getBookedUnder() {
        return bookedUnder;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getStayMonths() {
        return stayMonths;
    }

    public int getOccupants() {
        return occupants;
    }

    public BigDecimal getAccommodationFee() {
        return accommodationFee;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getHostelName() {
        return hostelName;
    }

    public int getId() {
      return id;
    }
}