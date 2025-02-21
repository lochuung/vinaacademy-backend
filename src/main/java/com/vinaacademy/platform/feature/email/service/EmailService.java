package com.vinaacademy.platform.feature.email.service;

public interface EmailService {

    void sendEmail(String toEmail, String subject, String body, boolean enableHtml);

    void sendEmailWithoutMQ(String toEmail, String subject, String body, boolean enableHtml);

    void sendEmailMQ(String toEmail, String subject, String body, boolean enableHtml);

}