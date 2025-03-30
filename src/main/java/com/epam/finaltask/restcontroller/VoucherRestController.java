package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.groups.CreateVoucherGroup;
import com.epam.finaltask.dto.RemoteResponse;
import com.epam.finaltask.dto.groups.UpdateVoucherStatusGroup;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Validated
public class VoucherRestController {
    private final VoucherService voucherService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> createVoucher(
            @Validated(CreateVoucherGroup.class) @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO createdVoucher = voucherService.create(voucherDTO);
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(List.of(createdVoucher),
                "Voucher is successfully created");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> updateVoucher(@PathVariable String id,
                                                                    @Validated(CreateVoucherGroup.class)
                                                                    @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updatedVoucher = voucherService.update(id, voucherDTO);
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(List.of(updatedVoucher),
                "Voucher is successfully updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RemoteResponse<VoucherDTO>> deleteVoucher(@PathVariable String id) {
        voucherService.delete(id);
        RemoteResponse<VoucherDTO> response = RemoteResponse.createSuccessResponse(List.of(),
                String.format("Voucher with Id %s has been deleted", id));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/order")
    public ResponseEntity<VoucherDTO> orderVoucher(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(voucherService.order(id, userId));
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
}
