package com.demo.kasi_bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryRequestDto {

    @NotBlank(message = "Account number cannot be blank")
    private String accountNumber;
}
