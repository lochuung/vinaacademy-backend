package com.vinaacademy.platform;

import com.vinaacademy.platform.feature.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@SpringBootApplication
public class VinaAcademyApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+07:00"));
        SpringApplication.run(VinaAcademyApplication.class, args);
    }

    @Bean
    public CommandLineRunner createTestingData(UserService userService) {
        return args -> {
            // Create testing data here
            userService.createTestingData();
        };
    }

}
