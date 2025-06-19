package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherDTO toVoucherDTO(Voucher voucher);

//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(voucherDTO.getId()))")
    Voucher toVoucher(VoucherDTO voucherDTO);

    @Mapping(target = "id", ignore = true)
    void updateVoucherFromDto(@MappingTarget Voucher voucher, VoucherDTO voucherDTO);
}
