package com.epam.finaltask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVouchersForReviewDTO {
    private String userId;
    private String username;
    private String email;
    private List<UserVoucherDTO> vouchers;
    private BigDecimal totalSpent; // Загальна сума всіх ваучерів
    private BigDecimal totalOrdersSum; // Нова поле для суми всіх замовлень

//    public UserVouchersForReviewDTO(String userId, String username, String email, Double totalSpent, Double totalOrdersSum) {
//        this.userId = userId;
//        this.username = username;
//        this.email = email;
//        this.vouchers = new ArrayList<>(); // Initialize empty list
//        this.totalSpent = totalSpent;
//        this.totalOrdersSum = totalOrdersSum;
//    }
}

