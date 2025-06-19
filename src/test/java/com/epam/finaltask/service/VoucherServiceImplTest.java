package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.impl.VoucherServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private VoucherMapper voucherMapper;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private Voucher voucher;
    private VoucherDTO voucherDTO;
    private UUID voucherId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        voucherId = UUID.randomUUID();
        userId = UUID.randomUUID();
        voucher = new Voucher();
        voucher.setId(voucherId);
        voucherDTO = new VoucherDTO();
        voucherDTO.setId(voucherId.toString());
    }

    @Test
    void create_ShouldSaveAndReturnVoucherDTO() {
        when(voucherMapper.toVoucher(voucherDTO)).thenReturn(voucher);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(voucherDTO);

        VoucherDTO result = voucherService.create(voucherDTO);

        assertNotNull(result);
        assertEquals(voucherId, UUID.fromString(result.getId()));
        verify(voucherRepository, times(1)).save(voucher);
    }

    @Test
    void order_ShouldThrowException_WhenVoucherNotFound() {
        when(voucherRepository.findById(voucherId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> voucherService.order(voucherId.toString(), userId.toString()));
    }

    @Test
    void update_ShouldUpdateVoucherAndReturnUpdatedDTO() {
        when(voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(voucherDTO);

        VoucherDTO result = voucherService.update(voucherId.toString(), voucherDTO);

        assertNotNull(result);
        assertEquals(voucherId.toString(), result.getId());
    }

    @Test
    void delete_ShouldDeleteVoucher() {
        doNothing().when(voucherRepository).deleteById(voucherId);

        voucherService.delete(voucherId.toString());

        verify(voucherRepository, times(1)).deleteById(voucherId);
    }

    @Test
    void findAllByPrice_ShouldReturnPagedVouchers() {
        Page<Voucher> page = new PageImpl<>(List.of(voucher));
        when(voucherRepository.findAllByPrice(anyDouble(), any(Pageable.class))).thenReturn(page);
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(voucherDTO);

        Page<VoucherDTO> result = voucherService.findAllByPrice(100.0, Pageable.unpaged());

        assertEquals(1, result.getContent().size());
    }
}

