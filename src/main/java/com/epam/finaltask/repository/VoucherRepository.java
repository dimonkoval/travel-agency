package com.epam.finaltask.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.Voucher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    @Query(value = "SELECT v.*, uv.status FROM traveldb.vouchers v " +
            "INNER JOIN traveldb.users_vouchers uv ON v.id = uv.voucher_id " +
            "WHERE uv.user_id = :userId",
            nativeQuery = true)
//    @Query(value = "SELECT v.*, uv.status FROM vouchers v " +
//            "INNER JOIN users_vouchers uv ON v.id = uv.voucher_id " +
//            "WHERE uv.user_id = :userId",
//            nativeQuery = true)
    List<Voucher> findAllByUserId(@Param("userId") UUID userId);
    Page<Voucher> findAllByTourType(TourType tourType, Pageable pageable);
    Page<Voucher> findAllByTransferType(TransferType transferType, Pageable pageable);
    Page<Voucher> findAllByPrice(Double price, Pageable pageable);
    Page<Voucher> findAllByHotelType(HotelType hotelType, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:title IS NULL OR LOWER(v.title) LIKE LOWER(CAST(CONCAT('%', :title, '%') AS string))) AND " +
            "(:tourType IS NULL OR v.tourType = :tourType) AND " +
            "(:hotelType IS NULL OR v.hotelType = :hotelType) AND " +
            "(:transferType IS NULL OR v.transferType = :transferType) AND " +
            "(:maxPrice IS NULL OR v.price <= :maxPrice)")
    Page<Voucher> search(
            @Param("title") String title,
            @Param("tourType") TourType tourType,
            @Param("hotelType") HotelType hotelType,
            @Param("transferType") TransferType transferType,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}
