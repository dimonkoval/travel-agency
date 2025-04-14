package com.epam.finaltask.repository;

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

}
