package com.example.unitedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UnitedServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnitedServiceApplication.class, args);
    }

}
