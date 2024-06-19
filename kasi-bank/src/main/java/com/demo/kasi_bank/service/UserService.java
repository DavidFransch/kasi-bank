package com.demo.kasi_bank.service;

import com.demo.kasi_bank.dto.AccountResponseDto;
import com.demo.kasi_bank.dto.UserRequestDto;

public interface UserService {

    AccountResponseDto createAccount(UserRequestDto userRequestDto);

}
