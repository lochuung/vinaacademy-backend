package com.vinaacademy.platform.feature.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@UtilityClass
@Log4j2
public class RandomUtils {

    private static final RandomStringUtils randomStringUtils = RandomStringUtils.secure();

    public static String generateRandomString(int length) {
        return randomStringUtils.nextAlphabetic(length);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateRandomNumber(int length) {
        return randomStringUtils.nextNumeric(length);
    }
}
