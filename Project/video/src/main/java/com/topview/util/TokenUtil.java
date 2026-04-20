package com.topview.util;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenUtil {
    // 简单缓存 Token,带有userId(也可用 Redis)
    private static final ConcurrentHashMap<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    private static final long TOKEN_EXPIRE_MS = 30 * 60 * 1000; // 30分钟

    static class TokenInfo {
        Long userId;
        long expireTime;

        TokenInfo(Long userId, long expireTime) {
            this.userId = userId;
            this.expireTime = expireTime;
        }
    }

    public static String generateToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long expireTime = System.currentTimeMillis() + TOKEN_EXPIRE_MS;
        tokenStore.put(token, new TokenInfo(userId, expireTime));
        return token;
    }

    public static Long getUserId(String token) {
        TokenInfo info = tokenStore.get(token);
        if (info == null) return null;
        if (System.currentTimeMillis() > info.expireTime) {
            tokenStore.remove(token); // 过期移除
            return null;
        }
        return info.userId;
    }

    /**
     * 刷新 Token：若剩余有效时间小于5分钟，则生成新 Token
     */
    public static String refreshTokenIfNeeded(String oldToken) {
        TokenInfo info = tokenStore.get(oldToken);
        if (info == null) return null;
        long now = System.currentTimeMillis();
        long left = info.expireTime - now;
        if (left < 5 * 60 * 1000) { // 少于5分钟
            tokenStore.remove(oldToken);
            return generateToken(info.userId);
        }
        return oldToken;
    }

    public static void removeToken(String token) {
        tokenStore.remove(token);
    }
}
