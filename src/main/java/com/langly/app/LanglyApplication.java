package com.langly.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.langly.app", "com.langly.security"})
public class LanglyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanglyApplication.class, args);
    }

}
