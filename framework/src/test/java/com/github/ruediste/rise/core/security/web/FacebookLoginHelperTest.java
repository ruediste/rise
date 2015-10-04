package com.github.ruediste.rise.core.security.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.ruediste.rise.core.security.web.FacebookLoginHelper.SignedRequest;

public class FacebookLoginHelperTest {
    String signed = "NNW1ipWTSkfr61-m_LdnE-3QEwTfPt6C9bzfIl4Sq30=.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImNvZGUiOiJBUURZek1pT1p6clVFSVhSRnc0Ym5xNERtNS0tLXJscGxqSGpKeXJldHdoYXh6QUlGeW9LZHhoTVdvaS14bGJISkNPd0lTWFBqdDBrblRBVEVFTnFWOEwyN2k2VnJ1SkFKQWFEdEl3WFJEVmo5XzFrb2Vfd1p6dENYOWNpLVNlR3F4TFEzNG84RzdoOEI0bUhuR0ZiRXoyMVgxLXYzUGR3NTIydTFDaE04NERDRXRCWUF0Sm9oYVg4M3dSZVozTjVBSVFiVmtUQ3B5ekxDS1ZJejNzOXlxei1jSVREc0lwdzFCQ0JNQzF3ZnphQ0tOTENyVjRGU0EtQVhwZ3JNMy0xM25iR0ZrWkVaR25abXROTWdTTWFZc0JWeE5Qb25pU0NDcDRMSVBMQjFDV1ltbjhYdGd6VTB4MGpYQU02dm1XMlY0OGV1RE5aaWxObi1lamJLUElWaWx0RCIsImlzc3VlZF9hdCI6MTQ0MzkxMjU1OCwidXNlcl9pZCI6IjEwMjA3NDM0ODYzOTAwMjE4In0";
    String secret = "1234";

    @Test
    public void testParseRequest() throws Exception {
        SignedRequest request = new FacebookLoginHelper().parseRequest(signed,
                secret);
        assertEquals(
                "AQDYzMiOZzrUEIXRFw4bnq4Dm5---rlpljHjJyretwhaxzAIFyoKdxhMWoi-xlbHJCOwISXPjt0knTATEENqV8L27i6VruJAJAaDtIwXRDVj9_1koe_wZztCX9ci-SeGqxLQ34o8G7h8B4mHnGFbEz21X1-v3Pdw522u1ChM84DCEtBYAtJohaX83wReZ3N5AIQbVkTCpyzLCKVIz3s9yqz-cITDsIpw1BCBMC1wfzaCKNLCrV4FSA-AXpgrM3-13nbGFkZEZGnZmtNMgSMaYsBVxNPoniSCCp4LIPLB1CWYmn8XtgzU0x0jXAM6vmW2V48euDNZilNn-ejbKPIViltD",
                request.code);
        assertEquals("10207434863900218", request.userId);
    }

    @Test(expected = RuntimeException.class)
    public void testParseRequest_mismatch_fails() throws Exception {
        new FacebookLoginHelper().parseRequest(signed, secret + "1");
    }

}
