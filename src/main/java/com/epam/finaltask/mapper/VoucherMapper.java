package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    @Mapping(source = "user.id", target = "userId")
    VoucherDTO toVoucherDTO(Voucher voucher);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    Voucher toVoucher(VoucherDTO voucherDTO);

    @Mapping(target = "id", ignore = true)
    void updateVoucherFromDto(@MappingTarget Voucher voucher, VoucherDTO voucherDTO);
}
