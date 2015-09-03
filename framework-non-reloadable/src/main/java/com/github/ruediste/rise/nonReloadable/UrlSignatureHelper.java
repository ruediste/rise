package com.github.ruediste.rise.nonReloadable;

import java.security.SecureRandom;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import com.google.common.hash.Hasher;

@NonRestartable
@Singleton
public class UrlSignatureHelper {

    private SecureRandom mainRandom = new SecureRandom();

    private ThreadLocal<SecureRandom> threadLocalRandom = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            byte[] seed = new byte[32];
            synchronized (mainRandom) {
                mainRandom.nextBytes(seed);
            }
            return new SecureRandom(seed);
        }
    };

    private byte[] secret;

    @PostConstruct
    public void postConstruct() {
        secret = new byte[32];
        mainRandom.nextBytes(secret);
    }

    private SecureRandom getRandom() {
        return threadLocalRandom.get();
    }

    public byte[] createSalt() {
        return createSalt(32);
    }

    public byte[] createSalt(int size) {
        byte[] result = new byte[size];
        getRandom().nextBytes(result);
        return result;
    }

    /**
     * Hash the secret. The secret is generated once per server start (non
     * restartable)
     */
    public void hashSecret(Hasher hasher) {
        hasher.putBytes(secret);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     * 
     * @param a
     *            the first byte array
     * @param b
     *            the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    public boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    public boolean slowEquals(String a, String b) {
        int diff = a.length() ^ b.length();
        for (int i = 0; i < a.length() && i < b.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
