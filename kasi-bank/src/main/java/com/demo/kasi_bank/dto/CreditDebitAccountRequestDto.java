package com.demo.kasi_bank.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDebitAccountRequestDto {

    private String accountNumber;
    private BigDecimal amount;
}
