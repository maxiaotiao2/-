package com.smilecat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.yaml")
public class EmailProperties {

    @Autowired
    private Environment env;

    public String getUsername() {
        return env.getProperty("spring.mail.username");
    }

    public String getPassword() {
        return env.getProperty("spring.mail.password");
    }
}