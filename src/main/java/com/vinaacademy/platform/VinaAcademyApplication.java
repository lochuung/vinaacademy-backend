package com.vinaacademy.platform;

import com.vinaacademy.platform.feature.TestingDataService;
import com.vinaacademy.platform.feature.common.constant.AppConstants;
import com.vinaacademy.platform.feature.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@SpringBootApplication
public class VinaAcademyApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(AppConstants.TIME_ZONE));
        SpringApplication.run(VinaAcademyApplication.class, args);
    }

    @Bean
    public CommandLineRunner createTestingData(TestingDataService testingDataService) {
        return args -> {
            // Create testing data here
            testingDataService.createTestingAuthData();
            testingDataService.createTestingCategoryData();
        };
    }

}
