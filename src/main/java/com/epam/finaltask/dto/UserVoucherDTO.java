package com.epam.finaltask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucherDTO {
    private String id;
    private String userId;
    private String voucherId;
    private String title;
    private String description;
    private BigDecimal price;
    private String tourType;
    private String transferType;
    private String hotelType;
    private String status;
    private LocalDate arrivalDate;
    private LocalDate evictionDate;
    private Boolean isHot;

    public UserVoucherDTO(String id, String title, BigDecimal price, String status,
                          LocalDate arrivalDate, LocalDate evictionDate) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.status = status;
        this.arrivalDate = arrivalDate;
        this.evictionDate = evictionDate;
    }
}
