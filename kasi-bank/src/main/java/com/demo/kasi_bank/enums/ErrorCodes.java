package com.demo.kasi_bank.enums;

public enum ErrorCodes {
    ACCOUNT_EXISTS("001", "This user already has an account created."),
    ACCOUNT_CREATION_SUCCESS("002", "Account has been successfully created."),
    ACCOUNT_NOT_EXIST("003", "User with the provided account does not exist."),
    ACCOUNT_FOUND("004", "User account found."),
    ACCOUNT_CREDITED_SUCCESS("005", "Account has been successfully credited."),
    ACCOUNT_BALANCE_INSUFFICIENT("006", "Insufficient balance."),
    ACCOUNT_DEBITED_SUCCESS("007", "Account has been successfully debited.");

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
