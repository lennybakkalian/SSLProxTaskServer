package io.sslprox.taskserver.utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import com.google.common.hash.Hashing;

public class CryptUtils {

    public final static String SECRET_KEY = "a3v890n57v37mzwvma387z9av35wmz879a3v5zm789w3g45n789gwz5";

    public static String toLongHash(String input) {
        return Hashing.sha512().hashString(SECRET_KEY + input, StandardCharsets.UTF_8).toString();
    }

    public static String toShortHash(String input) {
        return Hashing.sha1().hashString(SECRET_KEY + input, StandardCharsets.UTF_8).toString();
    }

    public static String generateRandomString(int length) {
        String result = "";
        char[] chars = "qwertzuiopasdfghjklyxcvbnmQWERTZUIOPASDFGHJKLYXCVBNM0123456789".toCharArray();
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < length; i++) {
            result += chars[rnd.nextInt(chars.length)];
        }
        return result;
    }
}
