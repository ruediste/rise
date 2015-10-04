package com.github.ruediste.rise.core.security.web;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.github.ruediste.rise.nonReloadable.SignatureHelper;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

public class FacebookLoginHelper {

    public static class SignedRequest {
        public String code;
        public String userId;
    }

    public SignedRequest parseRequest(String signedRequest, String secret) {
        int idx = signedRequest.indexOf('.');
        byte[] expectedSignature = BaseEncoding.base64Url()
                .decode(signedRequest.substring(0, idx));
        String encodedEnvelope = signedRequest.substring(idx + 1);

        SignatureHelper helper = new SignatureHelper();
        byte[] actualSignature = helper
                .createHasher(secret.getBytes(Charsets.UTF_8))
                .doFinal(encodedEnvelope.getBytes(Charsets.UTF_8));

        if (!helper.slowEquals(expectedSignature, actualSignature)) {
            throw new RuntimeException("Signatures don't match");
        }
        String envelope = new String(
                BaseEncoding.base64Url().decode(encodedEnvelope),
                Charsets.UTF_8);

        JSONObject parsed = (JSONObject) JSONValue.parse(envelope);
        SignedRequest result = new SignedRequest();
        result.code = (String) parsed.get("code");
        result.userId = (String) parsed.get("user_id");
        return result;
    }
}
