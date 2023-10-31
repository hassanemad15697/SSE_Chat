package com.test.pushnotification.config;

import com.test.pushnotification.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DefaultUsers {

    @Autowired
    private UserService userService;

    @Bean
    public void createDefaultUsers(){
        userService.addUser("hassan");
        userService.addUser("amro");
    }
}
