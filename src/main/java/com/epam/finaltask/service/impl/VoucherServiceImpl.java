package com.epam.finaltask.service.impl;

import static com.epam.finaltask.exception.StatusCodes.ENTITY_NOT_FOUND;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.UserVouchersForReviewDTO;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

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

        if (user.getBalance().compareTo(voucher.getPrice()) < 0) {
            throw new RuntimeException("Not enough money to buy this voucher.");
        }

        user.setBalance(user.getBalance().subtract(voucher.getPrice()));

        userRepository.save(user);

        userVoucher.setStatus(VoucherStatus.PAID);
        userVoucherRepository.save(userVoucher);
    }

    @Override
    public void cancelOrder(String userVoucherId) {

        UserVoucher userVoucher = userVoucherRepository.findById(UUID.fromString(userVoucherId))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

            userVoucher.setStatus(VoucherStatus.CANCELED);
            userVoucherRepository.save(userVoucher);
    }

    @Override
    public void cancelVoucher(UUID userVoucherId) {
        UserVoucher userVoucher = userVoucherRepository.findById(userVoucherId)
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
        if (userVoucher.getStatus() != VoucherStatus.CANCELED) {

            if (userVoucher.getStatus() == VoucherStatus.PAID) {
                User user = userVoucher.getUser();
                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                BigDecimal refundAmount = userVoucher.getVoucher().getPrice();

                user.setBalance(currentBalance.add(refundAmount));
            }
            userVoucher.setStatus(VoucherStatus.CANCELED);
            userVoucherRepository.save(userVoucher);
        }
    }

    @Override
    public Map<User, List<VoucherDTO>> getVouchersGroupedByUser() {
        List<UserVoucher> userVouchers = userVoucherRepository.findAllWithUserAndVoucher();

        return userVouchers.stream()
                .collect(Collectors.groupingBy(
                        UserVoucher::getUser,
                        Collectors.mapping(userVoucherMapper::toVoucherDTO, Collectors.toList())
                ));
    }

    @Override
    public List<VoucherDTO> getAllVouchersWithUsers() {
        return userVoucherRepository.findAllWithUserAndVoucher()
                .stream()
                .map(userVoucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsHot(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        voucher.setIsHot(true);
        voucherRepository.save(voucher);
    }

    public List<UserVouchersForReviewDTO> getUsersVouchersForReview() {
        List<UserVoucher> userVouchers = userVoucherRepository.findAllWithUserAndVoucher();

//        Map<User, List<UserVoucher>> grouped = userVouchers.stream()
//                .collect(Collectors.groupingBy(UserVoucher::getUser));

        Map<User, List<UserVoucher>> grouped = userVouchers.stream()
                .collect(Collectors.groupingBy(
                        UserVoucher::getUser,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));


        return grouped.entrySet().stream()
                .map(entry -> {
                    User user = entry.getKey();
                    List<UserVoucherDTO> voucherDTOs = entry.getValue().stream()
                            .map(userVoucherMapper::toUserVoucherDTO)
                            .sorted(Comparator.comparing(UserVoucherDTO::getTitle))
                            .collect(Collectors.toList());

                    BigDecimal totalSpent = voucherDTOs.stream()
                            .map(UserVoucherDTO::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Поки немає реальних даних по замовленнях — умовно вважаємо totalOrdersSum кількістю ваучерів (можеш замінити логікою з реальних даних)
                    BigDecimal totalOrdersSum = BigDecimal.valueOf(voucherDTOs.size());

                    return UserVouchersForReviewDTO.builder()
                            .userId(user.getId().toString())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .vouchers(voucherDTOs)
                            .totalSpent(totalSpent)
                            .totalOrdersSum(totalOrdersSum)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalOrders(List<UserVouchersForReviewDTO> usersVouchers) {
        return usersVouchers.stream()
                .map(UserVouchersForReviewDTO::getTotalOrdersSum)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalSpent(List<UserVouchersForReviewDTO> userVouchers) {
        return userVouchers.stream()
                .map(UserVouchersForReviewDTO::getTotalSpent)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
