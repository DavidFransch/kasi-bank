package com.demo.kasi_bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDebitAccountRequestDto {

    @NotBlank(message = "Account number cannot be blank")
    private String accountNumber;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
}
