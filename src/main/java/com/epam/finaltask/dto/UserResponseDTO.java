package com.epam.finaltask.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UserResponseDTO {
    private String username;
    private String email;
    private BigDecimal balance;
    private List<VoucherDTO> vouchers;
}
