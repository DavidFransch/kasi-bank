package com.demo.kasi_bank.service;

import com.demo.kasi_bank.dto.*;

public interface UserService {

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    AccountResponseDto createAccount(UserRequestDto userRequestDto);

    AccountResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto);

    String accountNameEnquiry(EnquiryRequestDto enquiryRequestDto);

    AccountResponseDto creditAccount(CreditDebitAccountRequestDto creditDebitAccountRequestDto);

    AccountResponseDto debitAccount(CreditDebitAccountRequestDto debitAccountRequestDto);

    AccountResponseDto transfer(TransferRequestDto transferRequestDto);

}
