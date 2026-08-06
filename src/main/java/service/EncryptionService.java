package com.example.securefilestoragesystem.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Service
public class EncryptionService {

    private static final String SECRET_KEY = "12345678901234567890123456789012";

    private final SecretKeySpec key =
            new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

    public byte[] encrypt(byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }
}