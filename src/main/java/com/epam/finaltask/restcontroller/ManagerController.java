package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserVouchersForReviewDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/api/manager")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@RequiredArgsConstructor
public class ManagerController {
    private final VoucherService voucherService;


    @GetMapping("/vouchers")
    public String getVouchersPage(Model model) {
        List<UserVouchersForReviewDTO> usersVouchers = voucherService.getUsersVouchersForReview();
        BigDecimal totalOrders = voucherService.getTotalOrders(usersVouchers);
        BigDecimal totalSpent = voucherService.getTotalSpent(usersVouchers);
        model.addAttribute("usersVouchers", usersVouchers);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("totalOrders", totalOrders);
        return "user/manager";
    }

    @PostMapping("/vouchers/{userVoucherId}/cancel")
    public String cancelVoucher(@PathVariable UUID userVoucherId,
                                RedirectAttributes redirectAttributes) {
        String message;
        try {
            voucherService.cancelVoucher(userVoucherId);
            message = "Voucher was successfully canceled.";
        } catch (EntityNotFoundException e) {
            message = "Voucher not found.";
        } catch (Exception e) {
            message = "An error occurred while canceling the voucher.";
        }

        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/api/manager/vouchers";
    }
}
