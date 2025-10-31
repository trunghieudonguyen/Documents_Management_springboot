package com.example.documentsmanagement.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte by : b) {
                sb.append(String.format("%02x", by));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA-256 algorithm available", e);
        }
    }
}
