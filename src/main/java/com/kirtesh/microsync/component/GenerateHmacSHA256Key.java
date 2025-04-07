package com.kirtesh.microsync.component;

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.util.Base64;

public class GenerateHmacSHA256Key {

    public static void main(String[] args) {
        try {
            // Create a KeyGenerator instance for HmacSHA256
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256); // Specify the key size (in bits)

            // Generate the secret key
            SecretKey secretKey = keyGenerator.generateKey();

            // Encode the key in Base64
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            // Print the Base64 encoded key
            System.out.println("Base64 Encoded HMAC SHA-256 Secret Key: " + base64Key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
