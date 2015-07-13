package com.github.ruediste.rise.testApp.assetDir;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import com.github.ruediste.rise.core.web.assetDir.AssetDirRequestMapper;
import com.github.ruediste.rise.testApp.WebTest;
import com.google.common.io.CharStreams;

public class TestAssetDirTest extends WebTest {

    @Inject
    AssetDirRequestMapper mapper;

    @Inject
    TestAssetDir dir;

    @Test
    public void testTxt() throws Exception {
        check("Test Text", "test.txt");
        check("function testJavascript(){}", "test.js");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(new HttpGet(url(mapper
                .getPathInfo(dir, ""))));
        assertEquals(404, response.getStatusLine().getStatusCode());
        client.close();
    }

    private void check(String expected, String subPath) throws IOException,
            UnsupportedEncodingException, MalformedURLException {
        String url = url(mapper.getPathInfo(dir, subPath));
        try (InputStream in = new URL(url).openStream()) {

            assertEquals(expected,
                    CharStreams.toString(new InputStreamReader(in, "utf-8")));
        }
    }
}
