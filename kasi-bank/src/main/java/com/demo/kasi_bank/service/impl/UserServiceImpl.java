package com.demo.kasi_bank.service.impl;

import com.demo.kasi_bank.dto.AccountInfoDto;
import com.demo.kasi_bank.dto.AccountResponseDto;
import com.demo.kasi_bank.dto.UserRequestDto;
import com.demo.kasi_bank.entity.User;
import com.demo.kasi_bank.enums.ErrorCodes;
import com.demo.kasi_bank.repository.UserRepository;
import com.demo.kasi_bank.service.UserService;
import com.demo.kasi_bank.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponseDto createAccount(final UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_EXISTS.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_EXISTS.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .address(userRequestDto.getAddress())
                .email(userRequestDto.getEmail())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .accountBalance(BigDecimal.ZERO)
                .accountNumber(AccountUtils.generateAccountNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        String accountName = savedUser.getFirstName() + " " + savedUser.getLastName();
        AccountInfoDto newAccountInfoDto = AccountInfoDto.builder()
                .accountName(accountName)
                .accountBalance(savedUser.getAccountBalance())
                .accountNumber(savedUser.getAccountNumber())
                .build();

        return AccountResponseDto.builder()
                .responseCode(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getCode())
                .responseMessage(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getMessage())
                .accountInfoDto(newAccountInfoDto)
                .build();
    }
}
