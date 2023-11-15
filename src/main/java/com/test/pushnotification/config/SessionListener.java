package com.test.pushnotification.config;

import com.test.pushnotification.singleton.ServerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
@Slf4j
public class SessionListener implements HttpSessionListener {


    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        // This is called when a session is created
        sessionEvent.getSession().setMaxInactiveInterval(9);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        // This is called when a session is destroyed
        String username = sessionEvent.getSession().getAttribute("username").toString();
        ServerManager.getUserByUsername(username).closeConnection();
        log.info("Session for user:({}) destroyed with Id: {}", username, sessionEvent.getSession().getId());
    }

}