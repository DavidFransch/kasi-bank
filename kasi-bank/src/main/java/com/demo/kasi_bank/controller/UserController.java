package com.demo.kasi_bank.controller;

import com.demo.kasi_bank.dto.AccountResponseDto;
import com.demo.kasi_bank.dto.CreditDebitAccountRequestDto;
import com.demo.kasi_bank.dto.EnquiryRequestDto;
import com.demo.kasi_bank.dto.UserRequestDto;
import com.demo.kasi_bank.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public AccountResponseDto createAccount(@RequestBody UserRequestDto userRequestDto) {
        return userService.createAccount(userRequestDto);
    }

    @GetMapping("/balance-enquiry")
    public AccountResponseDto balanceEnquiry(@RequestBody EnquiryRequestDto enquiryRequestDto) {
        return userService.balanceEnquiry(enquiryRequestDto);
    }

    @GetMapping("/name-enquiry")
    public String nameEnquiry(@RequestBody EnquiryRequestDto enquiryRequestDto) {
        return userService.accountNameEnquiry(enquiryRequestDto);
    }

    @PostMapping("/credit")
    public AccountResponseDto creditAccount(@RequestBody CreditDebitAccountRequestDto creditDebitAccountRequestDto) {
        return userService.creditAccount(creditDebitAccountRequestDto);
    }

    @PostMapping("/debit")
    public AccountResponseDto debitAccount(@RequestBody CreditDebitAccountRequestDto debitAccountRequestDto) {
        return userService.debitAccount(debitAccountRequestDto);
    }
}
