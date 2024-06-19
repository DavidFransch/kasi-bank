package com.demo.kasi_bank.utils;

import java.security.SecureRandom;
import java.time.Year;

public class AccountUtils {
    private static final SecureRandom random = new SecureRandom();

    public static String generateAccountNumber() {
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        int randomNumber = (int) Math.floor(random.nextInt() * (max - min + 1) + min);

        String year = String.valueOf(currentYear);
        String randomNumberString = String.valueOf(randomNumber);

        StringBuilder accountNumber = new StringBuilder(year);
        accountNumber.append(randomNumberString);

        return accountNumber.toString();
    }

}
