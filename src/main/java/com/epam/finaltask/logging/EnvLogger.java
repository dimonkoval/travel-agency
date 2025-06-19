package com.epam.finaltask.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EnvLogger {
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @EventListener(ApplicationReadyEvent.class)
    public void logEnv() {
//        System.out.println("🔥 Actual Google Redirect URI: " + redirectUri);
//        System.out.println("🔥 System Environment: " + System.getenv("GOOGLE_REDIRECT_URI"));
    }
}