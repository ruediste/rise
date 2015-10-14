package com.github.ruediste.rise.util;

import static org.junit.Assert.assertEquals;

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
}
