package com.epam.finaltask.dto;

import com.epam.finaltask.dto.groups.CreateVoucherGroup;
import com.epam.finaltask.dto.groups.UpdateVoucherStatusGroup;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.VoucherStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private Double price;
    private String tourType;
    private String transferType;
    private String hotelType;
    private String status;
    private LocalDate arrivalDate;
    private LocalDate evictionDate;
    private Boolean isHot;
}
