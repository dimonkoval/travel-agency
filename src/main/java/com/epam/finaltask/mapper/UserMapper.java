package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user);

    @Mapping(target = "balance", source = "user.balance", qualifiedByName = "doubleToBigDecimal")
    @Mapping(target = "vouchers", source = "vouchers")
    UserResponseDTO toUserResponseDTO(User user, List<VoucherDTO> vouchers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUser(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(Double balance) {
        return balance != null ? BigDecimal.valueOf(balance) : BigDecimal.ZERO;
    }
}
