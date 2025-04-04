package com.epam.finaltask.service.impl;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.UserRepository;
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

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.save(voucherMapper.toVoucher(voucherDTO));
        return voucherMapper.toVoucherDTO(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (voucher.getStatus() != VoucherStatus.REGISTERED) {
            throw new IllegalStateException("Voucher cannot be ordered");
        }

        voucher.setStatus(VoucherStatus.PAID);
        voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
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
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
        voucher.setIsHot(voucherDTO.getIsHot());
        voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        try {
            UUID uuid = UUID.fromString(userId);
            List<Voucher> vouchers = voucherRepository.findAllByUserId(uuid);
            if (vouchers == null) {
                return Collections.emptyList();
            }
            return vouchers.stream()
                    .map(voucherMapper::toVoucherDTO)
                    .toList();
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
        return voucherRepository.findAll().stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsHot(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
        voucher.setIsHot(true);
        voucherRepository.save(voucher);
    }
}
