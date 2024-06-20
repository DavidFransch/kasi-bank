package com.demo.kasi_bank.service;

import com.demo.kasi_bank.dto.AccountResponseDto;
import com.demo.kasi_bank.dto.CreditDebitAccountRequestDto;
import com.demo.kasi_bank.dto.EnquiryRequestDto;
import com.demo.kasi_bank.dto.UserRequestDto;

public interface UserService {

    AccountResponseDto createAccount(UserRequestDto userRequestDto);

    AccountResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto);

    String accountNameEnquiry(EnquiryRequestDto enquiryRequestDto);

    AccountResponseDto creditAccount(CreditDebitAccountRequestDto creditDebitAccountRequestDto);

    AccountResponseDto debitAccount(CreditDebitAccountRequestDto debitAccountRequestDto);

}
