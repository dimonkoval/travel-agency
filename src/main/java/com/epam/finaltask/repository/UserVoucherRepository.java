package com.epam.finaltask.repository;

import com.epam.finaltask.model.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, UUID> {
    @Query("SELECT uv FROM UserVoucher uv " +
            "JOIN FETCH uv.voucher v " +
            "WHERE uv.user.id = :userId")
    List<UserVoucher> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT uv FROM UserVoucher uv " +
            "JOIN FETCH uv.user u " +
            "JOIN FETCH uv.voucher v " +
            "ORDER BY u.username, v.title")
    List<UserVoucher> findAllWithUserAndVoucher();
}
