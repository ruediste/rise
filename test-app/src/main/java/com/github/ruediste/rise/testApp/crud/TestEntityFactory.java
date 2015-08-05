package com.github.ruediste.rise.testApp.crud;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.core.persistence.TransactionTemplate;

public class TestEntityFactory {

    @Inject
    TransactionTemplate trx;

    @Inject
    EntityManager em;

    public TestCrudEntityA testCrudEntityA() {
        TestCrudEntityA result = new TestCrudEntityA();
        result.setStringValue(randomString());
        return save(result);
    }

    private <T> T save(T result) {
        trx.updating().execute(() -> em.persist(result));
        return result;
    }

    private static Random random = new Random();
    static char[] randomChars;

    static {
        ArrayList<Character> tmp = new ArrayList<>();
        for (char ch = 'a'; ch <= 'z'; ch++)
            tmp.add(ch);
        for (char ch = 'A'; ch <= 'Z'; ch++)
            tmp.add(ch);
        for (char ch = '0'; ch <= '9'; ch++)
            tmp.add(ch);
        randomChars = new char[tmp.size()];
        for (int i = 0; i < tmp.size(); i++)
            randomChars[i] = tmp.get(i);
    }

    private String randomString() {
        return randomString(10);
    }

    private String randomString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(randomChars[random.nextInt(randomChars.length)]);
        }
        return sb.toString();
    }
}
