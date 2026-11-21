package com.hostel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserBooking {

    private int id;
    private int hostelId;
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

    // Display field obtained by joining hostels.
    private String hostelName;

    public UserBooking(
        int id,
        int hostelId,
        String bookedUnder,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int stayMonths,
        int occupants,
        BigDecimal accommodationFee,
        String bookingStatus,
        String paymentStatus,
        String email,
        String phone,
        String hostelName
    ) {
        this.id = id;
        this.hostelId = hostelId;
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
        this.hostelName = hostelName;
    }

    public int getId() {
        return id;
    }

    public int getHostelId() {
        return hostelId;
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

    public String getHostelName() {
        return hostelName;
    }
}
