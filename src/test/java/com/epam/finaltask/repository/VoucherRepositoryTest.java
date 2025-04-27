package com.epam.finaltask.repository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.Voucher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class VoucherRepositoryTest {

    @Mock
    private VoucherRepository voucherRepository;

    private UUID userId;
    private List<Voucher> voucherList;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        Voucher voucher = new Voucher();
        voucher.setId(UUID.randomUUID());
        voucher.setTourType(TourType.ECO);
        voucher.setTransferType(TransferType.BUS);
        voucher.setPrice(BigDecimal.valueOf(1001));
        voucher.setHotelType(HotelType.FIVE_STARS);

        voucherList = List.of(voucher);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findAllByUserId_ShouldReturnVouchers_WhenUserIdExists() {
        when(voucherRepository.findAllByUserId(userId)).thenReturn(voucherList);

        List<Voucher> result = voucherRepository.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByTourType_ShouldReturnPageOfVouchers_WhenTourTypeExists() {
        TourType tourType = TourType.ECO;
        Page<Voucher> page = new PageImpl<>(voucherList);

        when(voucherRepository.findAllByTourType(tourType, pageable)).thenReturn(page);

        Page<Voucher> result = voucherRepository.findAllByTourType(tourType, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(tourType, result.getContent().get(0).getTourType());
    }

    @Test
    void findAllByTransferType_ShouldReturnPageOfVouchers_WhenTransferTypeExists() {
        TransferType transferType = TransferType.BUS;
        Page<Voucher> page = new PageImpl<>(voucherList);

        when(voucherRepository.findAllByTransferType(transferType, pageable)).thenReturn(page);

        Page<Voucher> result = voucherRepository.findAllByTransferType(transferType, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(transferType, result.getContent().get(0).getTransferType());
    }

    @Test
    void findAllByHotelType_ShouldReturnPageOfVouchers_WhenHotelTypeExists() {
        HotelType hotelType = HotelType.FIVE_STARS;
        Page<Voucher> page = new PageImpl<>(voucherList);

        when(voucherRepository.findAllByHotelType(hotelType, pageable)).thenReturn(page);

        Page<Voucher> result = voucherRepository.findAllByHotelType(hotelType, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(hotelType, result.getContent().get(0).getHotelType());
    }
}

