package com.demo.kasi_bank.enums;

public enum ErrorCodes {
    ACCOUNT_EXISTS("001", "This user already has an account created."),
    ACCOUNT_CREATION_SUCCESS("002", "Account has been successfully created.");

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
