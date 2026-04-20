package com.topview.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
//密码加密工具
public class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成随机盐（Base64 编码）
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 对密码加盐哈希，返回哈希值（Base64）
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    /**
     * 验证密码
     */
    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        String newHash = hashPassword(inputPassword, salt);
        return newHash.equals(storedHash);
    }
}
