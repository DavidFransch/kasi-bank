package com.demo.kasi_bank.service;

import com.demo.kasi_bank.dto.AccountInfoDto;
import com.demo.kasi_bank.dto.AccountResponseDto;
import com.demo.kasi_bank.dto.UserRequestDto;
import com.demo.kasi_bank.entity.User;
import com.demo.kasi_bank.enums.ErrorCodes;
import com.demo.kasi_bank.enums.Role;
import com.demo.kasi_bank.repository.UserRepository;
import com.demo.kasi_bank.service.impl.UserServiceImpl;
import com.demo.kasi_bank.utils.AccountUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    @Spy
    private UserServiceImpl userService;

    @Test
    public void testCreateAccount_UserAlreadyExists() {
        // Given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Street")
                .email("existing@example.com")
                .password("password")
                .phoneNumber("1234567890")
                .build();

        // When
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);
        AccountResponseDto response = userService.createAccount(userRequestDto);

        // Then
        assertEquals(ErrorCodes.ACCOUNT_EXISTS.getCode(), response.getResponseCode());
        assertEquals(ErrorCodes.ACCOUNT_EXISTS.getMessage(), response.getResponseMessage());
        assertNull(response.getAccountInfoDto());

        verify(userService, never()).buildAccountInfo(any(User.class));
    }

    @Test
    public void testCreateAccount_Success() {
        // Given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Street")
                .email("newuser@example.com")
                .password("password")
                .phoneNumber("1234567890")
                .build();

        // When
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .address(userRequestDto.getAddress())
                .email(userRequestDto.getEmail())
                .password("encodedPassword") // Use mocked encoded password
                .phoneNumber(userRequestDto.getPhoneNumber())
                .accountBalance(BigDecimal.ZERO)
                .accountNumber(AccountUtils.generateAccountNumber())
                .status("ACTIVE")
                .role(Role.ROLE_ADMIN)
                .build();


        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AccountInfoDto accountInfoDto = new AccountInfoDto();
        doReturn(accountInfoDto).when(userService).buildAccountInfo(savedUser);

        AccountResponseDto response = userService.createAccount(userRequestDto);

        // Then
        assertEquals(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getCode(), response.getResponseCode());
        assertEquals(ErrorCodes.ACCOUNT_CREATION_SUCCESS.getMessage(), response.getResponseMessage());
        assertNotNull(response.getAccountInfoDto());
        assertEquals(accountInfoDto, response.getAccountInfoDto());

        verify(userService, times(1)).buildAccountInfo(savedUser);
    }

    @Test
    public void testCreateAccount_DataAccessException() {
        // Given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("123 Street")
                .email("newuser@example.com")
                .password("password")
                .phoneNumber("1234567890")
                .build();

        // When
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("...") {
        });

        AccountResponseDto response = userService.createAccount(userRequestDto);

        // Then
        assertEquals(ErrorCodes.DATA_ACCESS_ERROR.getCode(), response.getResponseCode());
        assertEquals(ErrorCodes.DATA_ACCESS_ERROR.getMessage(), response.getResponseMessage());
        assertNull(response.getAccountInfoDto());
    }
}
