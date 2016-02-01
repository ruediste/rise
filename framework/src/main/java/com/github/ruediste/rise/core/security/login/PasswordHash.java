package com.github.ruediste.rise.core.security.login;

import javax.persistence.Embeddable;

import com.github.ruediste1.i18n.label.PropertiesLabeled;

/**
 * Hash information of a password.
 */
@Embeddable
@PropertiesLabeled
public class PasswordHash {

    final String algorithm;
    final int iterations;
    final byte[] salt;
    final byte[] hash;

    /**
     * for persistence
     */
    public PasswordHash() {
        algorithm = null;
        iterations = 0;
        salt = null;
        hash = null;
    }

    public PasswordHash(String algorithm, int iterations, byte[] salt,
            byte[] hash) {
        super();
        this.iterations = iterations;
        this.algorithm = algorithm;
        this.salt = salt;
        this.hash = hash;
    }

    public int getIterations() {
        return iterations;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getSalt() {
        return salt;
    }

}
