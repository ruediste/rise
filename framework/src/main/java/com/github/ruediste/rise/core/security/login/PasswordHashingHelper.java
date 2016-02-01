package com.github.ruediste.rise.core.security.login;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.SignatureHelper;

@Singleton
public class PasswordHashingHelper {
    @Inject
    SignatureHelper signatureHelper;

    private String hashAlgorithm = "PBKDF2WithHmacSHA1";

    private int saltBytes = 24;
    private int hashBytes = 24;
    private int iterations = 10;

    /**
     * Returns a salted PBKDF2 hash of the password.
     * 
     * @param password
     *            the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public PasswordHash createHash(String password) {
        return createHash(password.toCharArray());
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     * 
     * @param password
     *            the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public PasswordHash createHash(char[] password) {
        // Generate a random salt
        byte[] salt = new byte[saltBytes];
        signatureHelper.getRandom().nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(hashAlgorithm, iterations, hashBytes, salt,
                password);
        // format iterations:salt:hash
        return new PasswordHash(hashAlgorithm, iterations, salt, hash);
    }

    /**
     * Validates a password using a hash.
     * 
     * @param password
     *            the password to check
     * @param goodHash
     *            the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public boolean validatePassword(String password, PasswordHash goodHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return validatePassword(password.toCharArray(), goodHash);
    }

    /**
     * Validates a password using a hash.
     * 
     * @param password
     *            the password to check
     * @param goodHash
     *            the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public boolean validatePassword(char[] password, PasswordHash goodHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(goodHash.getAlgorithm(),
                goodHash.getIterations(), goodHash.getHash().length,
                goodHash.getSalt(), password);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return signatureHelper.slowEquals(goodHash.getHash(), testHash);
    }

    /**
     * Computes the PBKDF2 hash of a password.
     * 
     * @param hashAlgorithm
     *            hash algorithm to use
     * @param iterations
     *            the iteration count (slowness factor)
     * @param passwordBytes
     *            the length of the hash to compute in bytes
     * @param salt
     *            the salt
     * @param password
     *            the password to hash.
     * 
     * @return the PBDKF2 hash of the password
     */
    private byte[] pbkdf2(String hashAlgorithm, int iterations,
            int passwordBytes, byte[] salt, char[] password) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations,
                passwordBytes * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory
                    .getInstance(getHashAlgorithm());
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(
                    "Error while hashing password. hashAlgorithm: "
                            + hashAlgorithm,
                    e);
        }
    }

    public int getSaltBytes() {
        return saltBytes;
    }

    /**
     * Set the number of bytes to be used for password salts. Can be changed
     * without affecting existing passwords
     */
    public void setSaltBytes(int saltBytes) {
        this.saltBytes = saltBytes;
    }

    public int getHashBytes() {
        return hashBytes;
    }

    /**
     * Set the number of bytes to be kept for password hashes. Can be chaged
     * without affecting existing passwords
     */
    public void setHashBytes(int hashBytes) {
        this.hashBytes = hashBytes;
    }

    public int getIterations() {
        return iterations;
    }

    /**
     * Set the number of iterations to be performed when hashing passwords. A
     * higher number means more cpu load on the server but also a higher
     * challenge for an attacker. Can be chaged without affecting existing
     * passwords
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * Set the password hashing algorithm to be used for new passwords. Can be
     * chaged without affecting existing passwords
     */
    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

}
