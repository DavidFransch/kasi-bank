package com.demo.kasi_bank.service.impl;

import com.demo.kasi_bank.dto.*;
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

        AccountInfoDto accountInfo = buildAccountInfo(savedUser);

        return AccountResponseDto.builder()
                .responseCode(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getCode())
                .responseMessage(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getMessage())
                .accountInfoDto(accountInfo)
                .build();
    }

    @Override
    public AccountResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto) {

        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequestDto.getAccountNumber());
        if (!isAccountExists) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequestDto.getAccountNumber());
        AccountInfoDto accountInfo = buildAccountInfo(foundUser);

        return AccountResponseDto.builder()
                .responseCode(ErrorCodes.ACCOUNT_FOUND.getCode())
                .responseMessage(ErrorCodes.ACCOUNT_FOUND.getMessage())
                .accountInfoDto(accountInfo)
                .build();
    }

    @Override
    public String accountNameEnquiry(EnquiryRequestDto enquiryRequestDto) {

        boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequestDto.getAccountNumber());
        if (!isAccountExists) {
            return ErrorCodes.ACCOUNT_NOT_EXIST.getMessage();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequestDto.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public AccountResponseDto creditAccount(CreditDebitAccountRequestDto creditDebitAccountRequestDto) {

        boolean isAccountExists = userRepository.existsByAccountNumber(creditDebitAccountRequestDto.getAccountNumber());
        if (!isAccountExists) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditDebitAccountRequestDto.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitAccountRequestDto.getAmount()));
        userRepository.save(userToCredit);

        AccountInfoDto accountInfo = buildAccountInfo(userToCredit);

        return AccountResponseDto.builder()
                .responseCode(ErrorCodes.ACCOUNT_CREDITED_SUCCESS.getCode())
                .responseMessage(ErrorCodes.ACCOUNT_CREDITED_SUCCESS.getMessage())
                .accountInfoDto(accountInfo)
                .build();
    }

    @Override
    public AccountResponseDto debitAccount(CreditDebitAccountRequestDto debitAccountRequestDto) {

        boolean isAccountExists = userRepository.existsByAccountNumber(debitAccountRequestDto.getAccountNumber());
        if (!isAccountExists) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(debitAccountRequestDto.getAccountNumber());
        BigDecimal currentAccountBalance = userToDebit.getAccountBalance();
        BigDecimal debitAmount = debitAccountRequestDto.getAmount();

        boolean isBalanceSufficient = currentAccountBalance.compareTo(debitAmount) >= 0;

        if (!isBalanceSufficient) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_BALANCE_INSUFFICIENT.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_BALANCE_INSUFFICIENT.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        BigDecimal updatedAccountBalance = currentAccountBalance.subtract(debitAmount);
        userToDebit.setAccountBalance(updatedAccountBalance);
        userRepository.save(userToDebit);

        AccountInfoDto accountInfo = buildAccountInfo(userToDebit);

        return AccountResponseDto.builder()
                .responseCode(ErrorCodes.ACCOUNT_DEBITED_SUCCESS.getCode())
                .responseMessage(ErrorCodes.ACCOUNT_DEBITED_SUCCESS.getMessage())
                .accountInfoDto(accountInfo)
                .build();
    }

    @Override
    public AccountResponseDto transfer(TransferRequestDto transferRequestDto) {

        // Check if the destination account exists
        boolean destinationAccountExists = userRepository.existsByAccountNumber(transferRequestDto.getSourceAccountNumber());
        if (!destinationAccountExists) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        // Check if the source account has sufficient funds
        User sourceAccount = userRepository.findByAccountNumber(transferRequestDto.getSourceAccountNumber());
        BigDecimal sourceAccountBalance = sourceAccount.getAccountBalance();
        BigDecimal transferAmount = transferRequestDto.getAmount();
        boolean isBalanceSufficient = sourceAccountBalance.compareTo(transferAmount) >= 0;

        if (!isBalanceSufficient) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_BALANCE_INSUFFICIENT.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_BALANCE_INSUFFICIENT.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        // Update the source account with the transfer amount
        sourceAccount.setAccountBalance(sourceAccountBalance.subtract(transferAmount));
        userRepository.save(sourceAccount);

        // Update destination account with transfer amount
        User destinationAccount = userRepository.findByAccountNumber(transferRequestDto.getDestinationAccountNumber());
        BigDecimal destinationAccountBalance = destinationAccount.getAccountBalance();
        destinationAccount.setAccountBalance(destinationAccountBalance.add(transferAmount));
        userRepository.save(destinationAccount);

        // Return the source account response
        AccountInfoDto sourceAccountInfo = buildAccountInfo(sourceAccount);

        return AccountResponseDto.builder()
                .responseCode(ErrorCodes.ACCOUNT_TRANSFER_SUCCESS.getCode())
                .responseMessage(ErrorCodes.ACCOUNT_TRANSFER_SUCCESS.getMessage())
                .accountInfoDto(sourceAccountInfo)
                .build();
    }

    private AccountInfoDto buildAccountInfo(User user) {
        return AccountInfoDto.builder()
                .accountName(user.getFirstName() + " " + user.getLastName())
                .accountBalance(user.getAccountBalance())
                .accountNumber(user.getAccountNumber())
                .build();
    }

}
