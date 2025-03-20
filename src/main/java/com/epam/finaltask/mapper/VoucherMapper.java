package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Voucher;

public interface VoucherMapper {
    Voucher toVoucher(VoucherDTO voucherDTO);
    VoucherDTO toVoucherDTO(Voucher voucher);
}
