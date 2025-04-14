package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.UserVoucher;
import com.epam.finaltask.model.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserVoucherMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "voucher", source = "voucher")
    @Mapping(target = "status", constant = "REGISTERED")
    @Mapping(target = "id", ignore = true)
    UserVoucher createUserVoucher(User user, Voucher voucher);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "id", source = "voucher.id")
    @Mapping(target = "title", source = "voucher.title")
    @Mapping(target = "description", source = "voucher.description")
    @Mapping(target = "price", source = "voucher.price")
    @Mapping(target = "tourType", source = "voucher.tourType")
    @Mapping(target = "transferType", source = "voucher.transferType")
    @Mapping(target = "hotelType", source = "voucher.hotelType")
    @Mapping(target = "arrivalDate", source = "voucher.arrivalDate")
    @Mapping(target = "evictionDate", source = "voucher.evictionDate")
    VoucherDTO toVoucherDTO(UserVoucher userVoucher);

    List<VoucherDTO> toVoucherDTOList(List<UserVoucher> userVouchers);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")  // Беремо ID користувача
    @Mapping(source = "voucher.id", target = "voucherId")  // Беремо ID ваучера
    @Mapping(source = "voucher.title", target = "title")  // Назва ваучера
    @Mapping(source = "voucher.description", target = "description")  // Опис ваучера
    @Mapping(source = "voucher.price", target = "price")  // Ціна ваучера
    @Mapping(source = "voucher.tourType", target = "tourType")  // Тип туру
    @Mapping(source = "voucher.transferType", target = "transferType")  // Тип трансферу
    @Mapping(source = "voucher.hotelType", target = "hotelType")  // Тип готелю
    @Mapping(source = "status", target = "status")  // Статус
    @Mapping(source = "voucher.arrivalDate", target = "arrivalDate")  // Дата приїзду
    @Mapping(source = "voucher.evictionDate", target = "evictionDate")  // Дата виселення
    @Mapping(source = "voucher.isHot", target = "isHot")
    UserVoucherDTO toUserVoucherDTO(UserVoucher userVoucher);
}
