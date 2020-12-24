package com.sen4ik.vfb.configs;

import com.sen4ik.vfb.filters.TwilioRequestValidatorFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;

    @Bean
    public FilterRegistrationBean<TwilioRequestValidatorFilter> twilioFilter(){
        FilterRegistrationBean<TwilioRequestValidatorFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TwilioRequestValidatorFilter(AUTH_TOKEN));
        registrationBean.addUrlPatterns("/twilio/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

}
