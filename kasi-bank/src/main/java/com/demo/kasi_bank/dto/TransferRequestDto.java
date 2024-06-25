package com.demo.kasi_bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    @NotBlank(message = "Source account number cannot be blank")
    private String sourceAccountNumber;

    @NotBlank(message = "Destination account number cannot be blank")
    private String destinationAccountNumber;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

}
