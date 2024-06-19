package com.demo.kasi_bank.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    private String responseCode;
    private String responseMessage;
    private AccountInfoDto accountInfoDto;

}
