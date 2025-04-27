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

}
