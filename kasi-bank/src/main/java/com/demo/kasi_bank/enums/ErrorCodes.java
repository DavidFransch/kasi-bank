package com.demo.kasi_bank.enums;

public enum ErrorCodes {
    ACCOUNT_EXISTS("001", "This user already has an account created."),
    ACCOUNT_CREATION_SUCCESS("002", "Account has been successfully created."),
    ACCOUNT_NOT_EXIST("003", "User with the provided account does not exist."),
    ACCOUNT_FOUND("004", "User account found."),
    ACCOUNT_CREDITED_SUCCESS("005", "Account has been successfully credited."),
    ACCOUNT_BALANCE_INSUFFICIENT("006", "Insufficient balance."),
    ACCOUNT_DEBITED_SUCCESS("007", "Account has been successfully debited."),
    ACCOUNT_TRANSFER_SUCCESS("008", "Account transfer success."),
    DATA_ACCESS_ERROR("009", "An data access error occurred while processing your request."),
    ARITHMETIC_CALCULATION_ERROR("010", "A arithmetic error occurred while processing your request."),
    ACCOUNT_LOGIN_SUCCESS("011", "Login Success");

    private final String code;
    private final String message;

    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
