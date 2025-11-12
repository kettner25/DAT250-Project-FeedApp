package no.hvl.group17.feedapp.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AnonCookieSigner {
    private static final String HMAC_ALGO = "HmacSHA256";

    private final byte[] secret;  // todo put this in config (env/secret manager); DO NOT hardcode in real prod

    public AnonCookieSigner(String secret) {
        this.secret = secret.getBytes();
    }

    public String sign(String anonId) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret, HMAC_ALGO));
            byte[] sig = mac.doFinal(anonId.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String anonId, String signature) {
        return signature != null && signature.equals(sign(anonId));
    }
}
