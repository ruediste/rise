package com.github.ruediste.rise.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class RiseUtilTest {
    @Test
    public void testResolvePath() throws Exception {
        assertEquals("/foo/bar", RiseUtil.resolvePath("/foo/", "bar"));
        assertEquals("/foo/bar", RiseUtil.resolvePath("/foo/test.txt", "bar"));
        assertEquals("foo/bar", RiseUtil.resolvePath("foo/", "bar"));
        assertEquals("foo/bar", RiseUtil.resolvePath("foo/", "./bar"));
        assertEquals("/bar", RiseUtil.resolvePath("foo/", "/bar"));
        assertEquals("foo1/bar", RiseUtil.resolvePath("foo1/foo2/", "../bar"));
        assertEquals("foo1/bar",
                RiseUtil.resolvePath("foo1/foo2/test.css", "../bar"));
    }

    @Test
    public void testRelativizePath() throws Exception {
        assertEquals("bar/test2.css",
                RiseUtil.relativizePath("/foo/", "/foo/bar/test2.css"));
        assertEquals("bar/test2.css",
                RiseUtil.relativizePath("/foo/test.css", "/foo/bar/test2.css"));
        assertEquals("../bar/test2.css",
                RiseUtil.relativizePath("/foo/foo1/", "/foo/bar/test2.css"));
        assertEquals("../bar/test2.css", RiseUtil
                .relativizePath("/foo/foo1/test.css", "/foo/bar/test2.css"));
    }

    @Test
    public void testGlobMatcher() {
        testGlob("*", "^\\Q\\E[^/]*$").matches("foo.js", "")
                .notMatches("app/foo.js");
        testGlob("**/*", "^\\Q\\E.*[^/]*$").matches("foo.js", "app/foo.js");
        testGlob("app/**/*", "^\\Qapp/\\E.*[^/]*$")
                .matches("app/foo.js", "app/src/foo.js")
                .notMatches("foo.js", "bar/foo.css");
    }

    private GlobTester testGlob(String glob, String expRegex) {
        return new GlobTester(glob, expRegex);
    }

    private static class GlobTester {
        Pattern regex;
        private String glob;

        public GlobTester(String glob, String expRegex) {
            this.glob = glob;
            String regexStr = RiseUtil.toRegex(glob);
            regex = Pattern.compile(regexStr);
            assertEquals(expRegex, regexStr);
        }

        public GlobTester matches(String... resources) {
            for (String resource : resources)
                assertTrue(glob + " matches " + resource,
                        regex.matcher(resource).matches());
            return this;
        }

        public GlobTester notMatches(String... resources) {
            for (String resource : resources)
                assertFalse(glob + " does not match " + resource,
                        regex.matcher(resource).matches());
            return this;
        }
    }
}
