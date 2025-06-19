package com.epam.finaltask.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VersionLogger implements CommandLineRunner {
    @Value("${build.version}")
    private String version;

    @Override
    public void run(String... args) {
        log.info("🚀 App version: {}", version);
    }
}

