package com.sen4ik.vfb.services;

import com.sen4ik.vfb.captcha.CaptchaSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("captchaService")
public class CaptchaService {

    @Autowired
    private CaptchaSettings captchaSettings;

    public String getReCaptchaSite() {
        return captchaSettings.getKeySite();
    }

    public String getReCaptchaSecret() {
        return captchaSettings.getSecretKey();
    }

}
