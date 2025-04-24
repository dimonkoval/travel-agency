package com.epam.finaltask.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.epam.finaltask.dto.UserVouchersForReviewDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.User;
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

    Page<VoucherDTO> searchVouchers(String title, TourType tourType, HotelType hotelType, TransferType transferType, Double price, Pageable pageable);

    VoucherDTO getById(String id);

    void buyVoucher(String userVoucherId);

    void cancelOrder(String userVoucherId);

    void cancelVoucher(UUID userVoucherId);

    Map<User, List<VoucherDTO>> getVouchersGroupedByUser();

    List<VoucherDTO> getAllVouchersWithUsers();

    List<UserVouchersForReviewDTO> getUsersVouchersForReview();

    BigDecimal getTotalOrders(List<UserVouchersForReviewDTO> usersVouchers);

    BigDecimal getTotalSpent(List<UserVouchersForReviewDTO> userVouchers);
}
