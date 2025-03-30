package com.epam.finaltask.service;

import java.util.List;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {
    VoucherDTO create(VoucherDTO voucherDTO);
    VoucherDTO order(String id, String userId);
    VoucherDTO update(String id, VoucherDTO voucherDTO);
    void delete(String voucherId);
    VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO);
    List<VoucherDTO> findAllByUserId(String userId);

    Page<VoucherDTO> findAllByTourType(TourType tourType, Pageable pageable);
    Page<VoucherDTO> findAllByTransferType(String transferType, Pageable pageable);
    Page<VoucherDTO> findAllByPrice(Double price, Pageable pageable);
    Page<VoucherDTO> findAllByHotelType(HotelType hotelType, Pageable pageable);

    List<VoucherDTO> findAll();
}
