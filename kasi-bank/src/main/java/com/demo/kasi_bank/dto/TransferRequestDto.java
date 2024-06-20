package com.demo.kasi_bank.dto;

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

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;

}
