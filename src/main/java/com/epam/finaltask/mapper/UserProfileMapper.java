package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "vouchers", source = "userVouchers")
    UserProfileDTO toUserProfileDTO(User user, List<UserVoucherDTO> userVouchers);
}
