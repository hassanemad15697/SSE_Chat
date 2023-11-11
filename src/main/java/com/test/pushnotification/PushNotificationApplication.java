package com.test.pushnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class PushNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PushNotificationApplication.class, args);
    }

}
