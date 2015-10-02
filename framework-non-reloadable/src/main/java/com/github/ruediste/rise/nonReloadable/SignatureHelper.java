package com.github.ruediste.rise.nonReloadable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Singleton;

@NonRestartable
@Singleton
public class SignatureHelper {

    public static final int SIGNATURE_LENGTH = 32;

    private SecureRandom mainRandom = new SecureRandom();

    private ThreadLocal<SecureRandom> threadLocalRandom = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            byte[] seed = new byte[SIGNATURE_LENGTH];
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

    public SecureRandom getRandom() {
        return threadLocalRandom.get();
    }

    public Mac createHasher() {
        try {
            String algorithm = "HmacSHA256";
            final SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            final Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);

            return mac;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Serialize an object (java serialization)
     * 
     * @param obj
     *            object to serialize
     * @param contextHasher
     *            function to hash context information, protecting against using
     *            the serialized representation in a different context
     * @see #deserializeSigned(byte[], Consumer)
     */
    public byte[] serializeSigned(Object obj, Consumer<Mac> contextHasher) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("Error during serialization", e);
        }

        Mac mac = createHasher();
        contextHasher.accept(mac);
        byte[] bb = baos.toByteArray();
        byte[] signature = mac.doFinal(bb);
        byte[] result = new byte[bb.length + signature.length];
        System.arraycopy(bb, 0, result, 0, bb.length);
        System.arraycopy(signature, 0, result, bb.length, signature.length);
        return result;
    }

    /**
     * deserialize a signed object
     * 
     * @param contextHasher
     *            function to hash context information, protecting against using
     *            the serialized representation in a different context
     * @see #serializeSigned(Object, Consumer)
     */
    public Object deserializeSigned(byte[] bb, Consumer<Mac> contextHasher) {
        // check signature
        Mac mac = createHasher();
        contextHasher.accept(mac);
        byte[] calcSign = mac.doFinal(
                Arrays.copyOfRange(bb, 0, bb.length - SIGNATURE_LENGTH));
        if (!slowEquals(calcSign, Arrays.copyOfRange(bb,
                bb.length - SIGNATURE_LENGTH, bb.length)))
            throw new RuntimeException("Signature did not match");

        // deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(bb);
        try (ObjectInputStream in = new ObjectInputStream(bais)) {
            return in.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Error during deserialization", e);
        }
    }
}
