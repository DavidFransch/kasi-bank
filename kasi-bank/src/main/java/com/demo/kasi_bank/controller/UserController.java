package com.demo.kasi_bank.controller;

import com.demo.kasi_bank.dto.*;
import com.demo.kasi_bank.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public AccountResponseDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/create")
    public AccountResponseDto createAccount(@RequestBody UserRequestDto userRequestDto) {
        return userService.createAccount(userRequestDto);
    }

    @Cacheable(cacheNames = "accountBalances", key = "#enquiryRequestDto.getAccountNumber()")
    @GetMapping("/balance-enquiry")
    public AccountResponseDto balanceEnquiry(@RequestBody EnquiryRequestDto enquiryRequestDto) {
        return userService.balanceEnquiry(enquiryRequestDto);
    }

    @GetMapping("/name-enquiry")
    public String nameEnquiry(@RequestBody EnquiryRequestDto enquiryRequestDto) {
        return userService.accountNameEnquiry(enquiryRequestDto);
    }

    @CacheEvict(cacheNames = "accountBalances", condition = "#result.responseCode == '005'", key = "#creditAccountRequestDto.getAccountNumber()")
    @PostMapping("/credit")
    public AccountResponseDto creditAccount(@RequestBody CreditDebitAccountRequestDto creditAccountRequestDto) {
        return userService.creditAccount(creditAccountRequestDto);
    }

    @CacheEvict(cacheNames = "accountBalances", condition = "#result.responseCode == '007'", key = "#debitAccountRequestDto.getAccountNumber()")
    @PostMapping("/debit")
    public AccountResponseDto debitAccount(@RequestBody CreditDebitAccountRequestDto debitAccountRequestDto) {
        return userService.debitAccount(debitAccountRequestDto);
    }

    @CacheEvict(cacheNames = "accountBalances",
            condition = "#result.responseCode == '008'",
            key = "#transferRequestDto.sourceAccountNumber.concat('-').concat(#transferRequestDto.destinationAccountNumber)")
    @PostMapping("/transfer")
    public AccountResponseDto transfer(@RequestBody TransferRequestDto transferRequestDto) {
        return userService.transfer(transferRequestDto);
    }
}
