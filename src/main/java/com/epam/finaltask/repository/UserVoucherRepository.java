package com.epam.finaltask.repository;

import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.UserVoucher;
import com.epam.finaltask.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, UUID> {
    Optional<UserVoucher> findByUserAndVoucher(User user, Voucher voucher);
    @Query("SELECT uv FROM UserVoucher uv " +
            "JOIN FETCH uv.voucher v " +
            "WHERE uv.user.id = :userId")
    List<UserVoucher> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT uv FROM UserVoucher uv " +
            "JOIN FETCH uv.user u " +
            "JOIN FETCH uv.voucher v " +
            "ORDER BY u.username, v.title")
    List<UserVoucher> findAllWithUserAndVoucher();

    @Query("SELECT new com.epam.finaltask.dto.UserVoucherDTO(" +
            "uv.id , " +
            "v.title, " +
            "v.price, " +
            "CAST(uv.status AS string), " +  // Конвертація Enum в String
            "v.arrivalDate, " +
            "v.evictionDate) " +
            "FROM UserVoucher uv " +
            "JOIN uv.voucher v " +
            "WHERE uv.user.id = :userId")
    List<UserVoucherDTO> findVouchersByUserId(@Param("userId") String userId);
}
