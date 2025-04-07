package com.kirtesh.microsync.component;

import java.security.SecureRandom;

public class TokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateToken() {
        int otp = RANDOM.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%0" + OTP_LENGTH + "d", otp); // Pads with leading zeros
    }
}
