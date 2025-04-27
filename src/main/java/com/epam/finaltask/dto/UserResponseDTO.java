package com.epam.finaltask.dto;

import com.epam.finaltask.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private BigDecimal balance;
    private Role role;
    private List<VoucherDTO> vouchers;
    private boolean active;
}
