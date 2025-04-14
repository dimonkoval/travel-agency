package com.epam.finaltask.service.impl;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.UserVoucherMapper;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.UserVoucher;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.UserVoucherRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.VoucherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static com.epam.finaltask.exception.StatusCodes.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;
    private final UserVoucherMapper userVoucherMapper;
    private final UserVoucherRepository userVoucherRepository;

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherMapper.toVoucher(voucherDTO);
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO order(String voucherId, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

//        Double userBalance = user.getBalance();
//        Double voucherPrice = voucher.getPrice();
//
//        if (user.getBalance() < voucher.getPrice()) {
//            throw new IllegalArgumentException("Not enough money to buy this voucher.");
//        }
//
//        user.setBalance(user.getBalance() - voucher.getPrice());
//        userRepository.save(user);

        UserVoucher userVoucher = userVoucherMapper.createUserVoucher(user, voucher);
//        userVoucher.setStatus(VoucherStatus.REGISTERED);
        userVoucherRepository.save(userVoucher);
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        voucherMapper.updateVoucherFromDto(voucher, voucherDTO);
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public void delete(String voucherId) {
        voucherRepository.deleteById(UUID.fromString(voucherId));
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        voucher.setIsHot(voucherDTO.getIsHot());
        voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        try {
            UUID uuid = UUID.fromString(userId);
//            List<Voucher> vouchers = voucherRepository.findAllByUserId(uuid);
            List<UserVoucher> userVouchers = userVoucherRepository.findAllByUserId(uuid);
            if (userVouchers == null) {
                return Collections.emptyList();
            }

            return userVoucherMapper.toVoucherDTOList(userVouchers);
//            return vouchers.stream()
//                    .map(voucherMapper::toVoucherDTO)
//                    .toList();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Невірний формат userId");
        }
    }

    @Override
    public Page<VoucherDTO> findAllByTourType(TourType tourType, Pageable pageable) {
        return voucherRepository.findAllByTourType(tourType, pageable)
                .map(voucherMapper::toVoucherDTO);
    }

    @Override
    public Page<VoucherDTO> findAllByTransferType(String transferType, Pageable pageable) {
        return voucherRepository.findAllByTransferType(TransferType.valueOf(transferType), pageable)
                .map(voucherMapper::toVoucherDTO);
    }

    @Override
    public Page<VoucherDTO> findAllByPrice(Double price, Pageable pageable) {
        return voucherRepository.findAllByPrice(price, pageable)
                .map(voucherMapper::toVoucherDTO);
    }

    @Override
    public Page<VoucherDTO> findAllByHotelType(HotelType hotelType, Pageable pageable) {
        return voucherRepository.findAllByHotelType(hotelType, pageable)
                .map(voucherMapper::toVoucherDTO);
    }

    @Override
    public List<VoucherDTO> findAll() {
//        return voucherRepository.findAllVouchers().stream()
//                .map(voucherMapper::toVoucherDTO)
//                .collect(Collectors.toList());
        return voucherRepository.findAll().stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VoucherDTO> searchVouchers(String title, TourType tourType, HotelType hotelType, TransferType transferType, Double maxPrice, Pageable pageable) {
        String titlePattern = null;
        if (title != null && !title.trim().isEmpty()) {
            titlePattern = "%" + title.toLowerCase() + "%"; // Формуємо шаблон на стороні Java
        }
        return voucherRepository.search(titlePattern, tourType, hotelType, transferType, maxPrice, pageable)
                .map(voucherMapper::toVoucherDTO);
    }

    @Override
    public VoucherDTO getById(String id) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public void buyVoucher(String userVoucherId) {
        UserVoucher userVoucher = userVoucherRepository.findById(UUID.fromString(userVoucherId))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        User user = userRepository.findById(userVoucher.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        Voucher voucher = voucherRepository.findById(userVoucher.getVoucher().getId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (user.getBalance() < voucher.getPrice()) {
            throw new RuntimeException("Not enough money to buy this voucher.");
        }

        user.setBalance(user.getBalance() - voucher.getPrice());
        userRepository.save(user);

        userVoucher.setStatus(VoucherStatus.PAID);
        userVoucherRepository.save(userVoucher);
    }

    @Override
    public void cancelOrder(String userVoucherId) {

        UserVoucher userVoucher = userVoucherRepository.findById(UUID.fromString(userVoucherId))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
//        User user = userRepository.findById(userVoucher.getUser().getId())
//                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
//            Voucher voucher = voucherRepository.findById(userVoucher.getVoucher().getId())
//                    .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));

//            UserVoucher userVoucher = userVoucherRepository.findByUserAndVoucher(user, voucher)
//                    .orElseThrow(() -> new EntityNotFoundException("UserVoucher not found"));

            // Зміна статусу на "CANCELED"
            userVoucher.setStatus(VoucherStatus.CANCELED);
            userVoucherRepository.save(userVoucher);
    }

    @Transactional
    public void markAsHot(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        voucher.setIsHot(true);
        voucherRepository.save(voucher);
    }
}
