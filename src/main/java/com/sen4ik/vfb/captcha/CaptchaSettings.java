package com.sen4ik.vfb.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "recaptcha.validation")
public class CaptchaSettings {

    private String keySite;
    private String secretKey;

    public String getKeySite() {
        return keySite;
    }

    public void setKeySite(String keySite) {
        this.keySite = keySite;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
