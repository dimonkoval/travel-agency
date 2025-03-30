package com.epam.finaltask.dto;

import com.epam.finaltask.dto.groups.CreateVoucherGroup;
import com.epam.finaltask.dto.groups.UpdateVoucherStatusGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class VoucherDTO {

    private String id;

    @NotBlank(message = "Title cannot be empty", groups = CreateVoucherGroup.class)
    @Length(max = 255, message = "Title is too long")
    private String title;

    @Schema(description = "Description of the tour")
    @Length(max = 500, message = "Description is too long")
    private String description;

    @NotNull(message = "Price cannot be null", groups = CreateVoucherGroup.class)
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Tour type cannot be null", groups = CreateVoucherGroup.class)
    private String tourType;

    @NotNull(message = "Transfer type cannot be null", groups = CreateVoucherGroup.class)
    private String transferType;

    @NotNull(message = "Hotel type cannot be null", groups = CreateVoucherGroup.class)
    private String hotelType;

    @NotNull(message = "Voucher status cannot be null", groups = CreateVoucherGroup.class)
    private String status;

    @NotNull(message = "Arrival date cannot be null", groups = CreateVoucherGroup.class)
    private LocalDate arrivalDate;

    @NotNull(message = "Eviction date cannot be null", groups = CreateVoucherGroup.class)
    private LocalDate evictionDate;

    @NotNull(message = "userId cannot be null", groups = CreateVoucherGroup.class)
    private UUID userId;

    @NotNull(message = "isHot cannot be null", groups = UpdateVoucherStatusGroup.class)
    private Boolean isHot;
}
