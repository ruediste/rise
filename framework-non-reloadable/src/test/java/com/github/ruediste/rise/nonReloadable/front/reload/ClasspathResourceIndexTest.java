package com.github.ruediste.rise.nonReloadable.front.reload;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class ClasspathResourceIndexTest {

    @Test
    public void test() {
        ClasspathResourceIndex idx = new ClasspathResourceIndex();
        idx.resources.addAll(Arrays.asList("/foo.js", "/foo/bar.js", "/bar/foo.js", "/foo/foo.js"));
        assertThat(idx.getResourcesByGlob("**/foo.js"), containsInAnyOrder("/foo.js", "/bar/foo.js", "/foo/foo.js"));
        assertThat(idx.getResourcesByGlob("/foo/*"), containsInAnyOrder("/foo/bar.js", "/foo/foo.js"));
    }
}
