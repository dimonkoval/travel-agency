package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.groups.CreateVoucherGroup;
import com.epam.finaltask.dto.RemoteResponse;
import com.epam.finaltask.dto.groups.UpdateVoucherStatusGroup;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Validated
public class VoucherRestController {
    private final VoucherService voucherService;
    private final UserService userService;

    @GetMapping({"/new", "/{id}/edit"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String showVoucherForm(
            @PathVariable(required = false) String id,
            Model model) {

        VoucherDTO voucher = id != null
                ? voucherService.getById(id)
                : new VoucherDTO();

        model.addAttribute("voucher", voucher);
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("transferTypes", TransferType.values());
        return "voucher/form"; // єдина форма
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createVoucher(@Validated(CreateVoucherGroup.class) @ModelAttribute VoucherDTO voucherDTO,
                                RedirectAttributes redirectAttributes) {
        voucherService.create(voucherDTO);
        redirectAttributes.addFlashAttribute("message", "Voucher created!");
        return "redirect:/user/dashboard";
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String updateVoucher(@PathVariable String id,
                                @ModelAttribute("voucher") VoucherDTO voucherDTO,
                                RedirectAttributes redirectAttributes) {
        log.info("Updating voucher with ID: {}", id);
        log.info("Received data: {}", voucherDTO);
        voucherService.update(id, voucherDTO);
        redirectAttributes.addFlashAttribute("message", "Voucher updated successfully");
        return "redirect:/user/dashboard";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> deleteVoucher(@PathVariable String id) {
        voucherService.delete(id);
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(List.of(),
                String.format("Voucher with Id %s has been deleted", id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/order")
    public String orderVoucher(
            @PathVariable String id,
            @RequestParam String userId,
            RedirectAttributes redirectAttributes) {
        try {
            voucherService.order(id, userId);
            redirectAttributes.addFlashAttribute("message", "The voucher has been successfully booked!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/user/dashboard";
    }

    @PostMapping("/buy")
    public String buyVoucher(@RequestParam String userVoucherId,
                             @RequestParam String userId,
                             RedirectAttributes redirectAttributes) {
        UserDTO user = userService.getUserById(UUID.fromString(userId));
        try {
            voucherService.buyVoucher(userVoucherId);
            redirectAttributes.addFlashAttribute("message", "Voucher successfully purchased!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/api/users/profile/" + user.getUsername();
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam String userVoucherId,
                              @RequestParam String userId,
                              RedirectAttributes redirectAttributes) {
        UserDTO user = userService.getUserById(UUID.fromString(userId));

        try {
            voucherService.cancelOrder(userVoucherId);
            redirectAttributes.addFlashAttribute("message", "Voucher successfully canceled!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/api/users/profile/" + user.getUsername();
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> changeHotStatus(@PathVariable String id,
                                                                      @Validated(UpdateVoucherStatusGroup.class)
                                                                      @RequestBody VoucherDTO voucherDTO) {
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(
                List.of(voucherService.changeHotStatus(id, voucherDTO)),
                "Voucher status is successfully changed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RemoteResponse<VoucherDTO>> getVouchersByUser(@PathVariable String userId) {
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(vouchers, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-tour-type")
    public ResponseEntity<Page<VoucherDTO>> getVouchersByTourType(@RequestParam String tourType,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(voucherService.findAllByTourType(Enum.valueOf(TourType.class, tourType), pageable));
    }

    @GetMapping("/by-transfer-type")
    public ResponseEntity<Page<VoucherDTO>> getVouchersByTransferType(@RequestParam String transferType,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(voucherService.findAllByTransferType(transferType, pageable));
    }

    @GetMapping("/by-price")
    public ResponseEntity<Page<VoucherDTO>> getVouchersByPrice(@RequestParam String price,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(voucherService.findAllByPrice(Double.parseDouble(price), pageable));
    }

    @GetMapping("/by-hotel-type")
    public ResponseEntity<Page<VoucherDTO>> getVouchersByHotelType(@RequestParam String hotelType,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(voucherService.findAllByHotelType(Enum.valueOf(HotelType.class, hotelType), pageable));
    }

    @GetMapping
    public ResponseEntity<RemoteResponse<VoucherDTO>> getAllVouchers() {
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(
                voucherService.findAll(), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VoucherDTO>> searchVouchers(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TourType tourType,
            @RequestParam(required = false) HotelType hotelType,
            @RequestParam(required = false) TransferType transferType,
            @RequestParam(required = false) Double price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Sort.Direction direction = Sort.Direction.fromString(dir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<VoucherDTO> result = voucherService.searchVouchers(title, tourType, hotelType, transferType, price, pageable);
        return ResponseEntity.ok(result);
    }

}
