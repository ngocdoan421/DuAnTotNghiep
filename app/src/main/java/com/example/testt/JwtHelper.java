package com.example.testt;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JwtHelper {
    private static final String SECRET_KEY = "my_super_secret_key_for_jwt_generation_in_this_app_needs_to_be_long_enough";

    // Cài thời gian hết hạn cho Token (ví dụ: 1 giờ = 3600000 ms)
    private static final long EXPIRATION_TIME_MS = 3600000; 

    public static String generateToken(String userId, String email) {
        try {
            // Header
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String encodedHeader = Base64.encodeToString(header.toString().getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);

            // Payload
            JSONObject payload = new JSONObject();
            payload.put("userId", userId);
            payload.put("email", email);
            long currentTime = System.currentTimeMillis();
            payload.put("iat", currentTime / 1000);
            payload.put("exp", (currentTime + EXPIRATION_TIME_MS) / 1000); // Expiration time
            String encodedPayload = Base64.encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);

            // Signature
            String signatureInput = encodedHeader + "." + encodedPayload;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(signatureInput.getBytes(StandardCharsets.UTF_8));
            String encodedSignature = Base64.encodeToString(signatureBytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);

            return encodedHeader + "." + encodedPayload + "." + encodedSignature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String signatureInput = parts[0] + "." + parts[1];
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] expectedSignatureBytes = mac.doFinal(signatureInput.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.encodeToString(expectedSignatureBytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);

            if (!expectedSignature.equals(parts[2])) {
                return false; // Invalid signature
            }

            // Check expiration
            String payloadStr = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadStr);
            long exp = payload.getLong("exp");
            if (System.currentTimeMillis() / 1000 > exp) {
                return false; // Token expired
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
