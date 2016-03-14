package com.github.ruediste.rise.nonReloadable;

import static org.junit.Assert.assertEquals;

import java.util.function.Consumer;

import javax.crypto.Mac;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;

public class SignatureHelperTest {

    SignatureHelper helper;

    @Before
    public void before() {
        helper = new SignatureHelper();
        helper.postConstruct();
    }

    @Test
    public void serializationRoundTrip() {
        Consumer<Mac> ctx = mac -> mac.update("ctx".getBytes(Charsets.UTF_8));
        byte[] bb = helper.serializeSigned("foo", ctx);
        assertEquals("foo", helper.deserializeSigned(bb, ctx));
    }

    @Test(expected = RuntimeException.class)
    public void serializationRoundTrip_differentContext_fails() {
        byte[] bb = helper.serializeSigned("foo", mac -> mac.update("ctx".getBytes(Charsets.UTF_8)));
        assertEquals("foo", helper.deserializeSigned(bb, mac -> mac.update("cty".getBytes(Charsets.UTF_8))));
    }

    @Test(expected = RuntimeException.class)
    public void serializationRoundTrip_bitchange_fails() {
        Consumer<Mac> ctx = mac -> mac.update("ctx".getBytes(Charsets.UTF_8));
        byte[] bb = helper.serializeSigned("foo", ctx);
        bb[bb.length - 1] = (byte) (bb[bb.length - 1] ^ ((byte) 1));
        assertEquals("foo", helper.deserializeSigned(bb, ctx));
    }

}
