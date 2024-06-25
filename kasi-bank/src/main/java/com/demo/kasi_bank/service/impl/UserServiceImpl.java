package com.demo.kasi_bank.service.impl;

import com.demo.kasi_bank.config.JwtTokenProvider;
import com.demo.kasi_bank.dto.*;
import com.demo.kasi_bank.entity.User;
import com.demo.kasi_bank.enums.ErrorCodes;
import com.demo.kasi_bank.enums.Role;
import com.demo.kasi_bank.repository.UserRepository;
import com.demo.kasi_bank.service.UserService;
import com.demo.kasi_bank.utils.AccountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final AuthenticationManager authenticationManager, final JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
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

        try {
            User newUser = User.builder()
                    .firstName(userRequestDto.getFirstName())
                    .lastName(userRequestDto.getLastName())
                    .address(userRequestDto.getAddress())
                    .email(userRequestDto.getEmail())
                    .password(passwordEncoder.encode(userRequestDto.getPassword()))
                    .phoneNumber(userRequestDto.getPhoneNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .status("ACTIVE")
                    .role(Role.ROLE_ADMIN)
                    .build();

            User savedUser = userRepository.save(newUser);

            AccountInfoDto accountInfo = buildAccountInfo(savedUser);

            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getMessage())
                    .accountInfoDto(accountInfo)
                    .build();

        } catch (DataAccessException e) {
            log.error("Error creating user account", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.DATA_ACCESS_ERROR.getCode())
                    .responseMessage(ErrorCodes.DATA_ACCESS_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();
        }
    }

    @Override
    public AccountResponseDto balanceEnquiry(final EnquiryRequestDto enquiryRequestDto) {

        if (!isAccountExists(enquiryRequestDto.getAccountNumber())) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        try {
            User foundUser = userRepository.findByAccountNumber(enquiryRequestDto.getAccountNumber());
            AccountInfoDto accountInfo = buildAccountInfo(foundUser);

            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_FOUND.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_FOUND.getMessage())
                    .accountInfoDto(accountInfo)
                    .build();

        } catch (DataAccessException e) {
            log.error("Error during data access operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.DATA_ACCESS_ERROR.getCode())
                    .responseMessage(ErrorCodes.DATA_ACCESS_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

    }

    @Override
    public String accountNameEnquiry(final EnquiryRequestDto enquiryRequestDto) {

        if (!isAccountExists(enquiryRequestDto.getAccountNumber())) {
            return ErrorCodes.ACCOUNT_NOT_EXIST.getMessage();
        }
        try {
            User foundUser = userRepository.findByAccountNumber(enquiryRequestDto.getAccountNumber());
            return foundUser.getFirstName() + " " + foundUser.getLastName();

        } catch (DataAccessException e) {
            log.error("Error during data access operation", e);
            return ErrorCodes.DATA_ACCESS_ERROR.getMessage();
        }
    }

    @Override
    public AccountResponseDto creditAccount(final CreditDebitAccountRequestDto creditDebitAccountRequestDto) {

        if (!isAccountExists(creditDebitAccountRequestDto.getAccountNumber())) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }
        try {
            User userToCredit = userRepository.findByAccountNumber(creditDebitAccountRequestDto.getAccountNumber());
            userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitAccountRequestDto.getAmount()));
            userRepository.save(userToCredit);

            AccountInfoDto accountInfo = buildAccountInfo(userToCredit);

            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_CREDITED_SUCCESS.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_CREDITED_SUCCESS.getMessage())
                    .accountInfoDto(accountInfo)
                    .build();

        } catch (DataAccessException e) {
            log.error("Error during data access operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.DATA_ACCESS_ERROR.getCode())
                    .responseMessage(ErrorCodes.DATA_ACCESS_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();

        } catch (ArithmeticException e) {
            log.error("Error performing credit operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ARITHMETIC_CALCULATION_ERROR.getCode())
                    .responseMessage(ErrorCodes.ARITHMETIC_CALCULATION_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();
        }
    }

    @Override
    public AccountResponseDto debitAccount(final CreditDebitAccountRequestDto debitAccountRequestDto) {

        if (!isAccountExists(debitAccountRequestDto.getAccountNumber())) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        try {
            User userToDebit = userRepository.findByAccountNumber(debitAccountRequestDto.getAccountNumber());
            BigDecimal currentAccountBalance = userToDebit.getAccountBalance();
            BigDecimal debitAmount = debitAccountRequestDto.getAmount();

            if (!isBalanceSufficient(currentAccountBalance, debitAmount)) {
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
        } catch (DataAccessException e) {
            log.error("Error during data access operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.DATA_ACCESS_ERROR.getCode())
                    .responseMessage(ErrorCodes.DATA_ACCESS_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();

        } catch (ArithmeticException e) {
            log.error("Error performing credit operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ARITHMETIC_CALCULATION_ERROR.getCode())
                    .responseMessage(ErrorCodes.ARITHMETIC_CALCULATION_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

    }

    @Override
    public AccountResponseDto transfer(final TransferRequestDto transferRequestDto) {

        // Check if the destination account exists
        boolean destinationAccountExists = userRepository.existsByAccountNumber(transferRequestDto.getSourceAccountNumber());
        if (!destinationAccountExists) {
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ACCOUNT_NOT_EXIST.getCode())
                    .responseMessage(ErrorCodes.ACCOUNT_NOT_EXIST.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

        try {
            // Check if the source account has sufficient funds
            User sourceAccount = userRepository.findByAccountNumber(transferRequestDto.getSourceAccountNumber());
            BigDecimal sourceAccountBalance = sourceAccount.getAccountBalance();
            BigDecimal transferAmount = transferRequestDto.getAmount();

            if (!isBalanceSufficient(sourceAccountBalance, transferAmount)) {
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

        } catch (DataAccessException e) {
            log.error("Error during data access operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.DATA_ACCESS_ERROR.getCode())
                    .responseMessage(ErrorCodes.DATA_ACCESS_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();
        } catch (ArithmeticException e) {
            log.error("Error performing credit operation", e);
            return AccountResponseDto.builder()
                    .responseCode(ErrorCodes.ARITHMETIC_CALCULATION_ERROR.getCode())
                    .responseMessage(ErrorCodes.ARITHMETIC_CALCULATION_ERROR.getMessage())
                    .accountInfoDto(null)
                    .build();
        }

    }

    @Override
    public AccountResponseDto login(final LoginDto loginDto) {
        Authentication authentication;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        return AccountResponseDto.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();

    }

    private boolean isAccountExists(final String accountNumber) {
        return userRepository.existsByAccountNumber(accountNumber);
    }

    private boolean isBalanceSufficient(final BigDecimal accountBalance, final BigDecimal amount) {
        return accountBalance.compareTo(amount) >= 0;
    }

    private AccountInfoDto buildAccountInfo(final User user) {
        return AccountInfoDto.builder()
                .accountName(user.getFirstName() + " " + user.getLastName())
                .accountBalance(user.getAccountBalance())
                .accountNumber(user.getAccountNumber())
                .build();
    }

}
