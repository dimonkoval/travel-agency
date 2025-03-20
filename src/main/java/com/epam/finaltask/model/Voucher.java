package com.epam.finaltask.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;

@Entity
public class Voucher {

    private UUID id;

    private String title;

    private String description;

    private Double price;

    private TourType tourType;

    private TransferType transferType;

    private HotelType hotelType;

    private VoucherStatus status;

    private LocalDate arrivalDate;

    private LocalDate evictionDate;

    private User user;

    private boolean isHot;


}
