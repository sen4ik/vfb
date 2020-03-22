package com.sen4ik.vfb.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
@Getter
@Setter
public class CaptchaSettings {
    private String site;
    private String secret;
}
