package com.vinaacademy.platform.feature.email.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.email.EmailAccountUsageRepository;
import com.vinaacademy.platform.feature.email.config.MailProperties;
import com.vinaacademy.platform.feature.email.dto.EmailMessage;
import com.vinaacademy.platform.feature.email.entity.EmailAccountUsage;
import com.vinaacademy.platform.feature.email.mq.redis.EmailProducer;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EmailServiceImpl implements EmailService {

    public static final String GMAIL_PROVIDER = "gmail";
    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private EmailAccountUsageRepository emailAccountUsageRepository;

    @Autowired(required = false)
    private EmailProducer emailProducer;

    @Value("${spring.data.redis.enabled:false}")
    private boolean redisEnabled;

    @Override
    public void sendEmail(String toEmail, String subject, String body, boolean enableHtml) {
        if (redisEnabled) {
            log.info("Sending email with redis mq...");
            sendEmailMQ(toEmail, subject, body, enableHtml);
        } else {
            log.info("Sending email direct....");
            sendEmailWithoutMQ(toEmail, subject, body, enableHtml);
        }
    }

    @Override
    public void sendEmailWithoutMQ(String toEmail, String subject, String body, boolean enableHtml) {
        if (mailProperties.isOnlyGmail()) {
            log.info("reset gmail count");
            emailAccountUsageRepository.resetEmailCountsIfNotUpdatedToday(LocalDate.now());
        }

        JavaMailSenderImpl mailSender = getSender();
        String sender = mailSender.getUsername();
        MimeMessage message = getMimeMessage(toEmail, subject, body, sender, enableHtml, mailSender);

        Instant start = Instant.now();

        mailSender.send(message);

        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();

        log.info("send smtp gmail takes {} ms", timeElapsed);
        if (mailProperties.isOnlyGmail()) {
            updateEmailUsage(sender);
        }
    }

    @Override
    public void sendEmailMQ(String toEmail, String subject, String body, boolean enableHtml) {
        EmailMessage emailMessage = new EmailMessage(toEmail, subject, body, enableHtml);
        emailProducer.enqueueEmail(emailMessage);
    }

    private static MimeMessage getMimeMessage(String toEmail, String subject, String body, String sender,
                                              boolean enableHtml, JavaMailSenderImpl mailSender) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        try {
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, enableHtml);
            helper.setFrom(sender);
        } catch (Exception e) {
            log.error("Error creating mime message", e);
        }
        return message;
    }

    private static SimpleMailMessage getMailMessage(String toEmail, String subject, String body, String sender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(sender);
        return message;
    }

    private JavaMailSenderImpl getSender() {
        List<String> providers = mailProperties.getActiveProviders();
        if (CollectionUtils.isEmpty(providers)) {
            throw BadRequestException.message("No mail providers provided.");
        }

        JavaMailSenderImpl mailSender = getMailSenderByProvider(providers);
        if (mailSender == null) {
            throw BadRequestException.message("No mail sender available for the specified provider.");
        }
        return mailSender;
    }

    private JavaMailSenderImpl getMailSenderByProvider(List<String> providers) {
        List<MailProperties.MailAccount> accounts = getEmailAccounts();

        List<MailProperties.MailAccount> filteredAccounts = filterByEmailUsage(providers, accounts);
        if (filteredAccounts.isEmpty()) {
            return null;
        }

        if (mailProperties.isUseRandom()) {
            MailProperties.MailAccount randomAccount = filteredAccounts.get(new Random().nextInt(filteredAccounts.size()));
            return createMailSender(randomAccount);
        }
        return createMailSender(filteredAccounts.get(0));
    }

    private List<MailProperties.MailAccount> filterByEmailUsage(List<String> providers, List<MailProperties.MailAccount> accounts) {
        //only apply limit for gmail
        if (mailProperties.isOnlyGmail()) {
            Map<String, EmailAccountUsage> usageMap = emailAccountUsageRepository.findAll().stream().collect(Collectors.toMap(EmailAccountUsage::getUsername, Function.identity(), (o, n) -> n));

            return accounts.stream().filter(acc -> providers.contains(acc.getProvider()) && hasRemainingQuota(acc.getUsername(), usageMap)).toList();
        }
        return accounts;
    }

    private List<MailProperties.MailAccount> getEmailAccounts() {
        if (CollectionUtils.isEmpty(mailProperties.getAccounts())) {
            throw BadRequestException.message("Missing account email configuration.");
        }
        if (mailProperties.isOnlyGmail()) {
            return mailProperties.getAccounts().stream().filter(s -> GMAIL_PROVIDER.equalsIgnoreCase(s.getProvider())).toList();
        }
        return mailProperties.getAccounts();
    }

    private JavaMailSenderImpl createMailSender(MailProperties.MailAccount account) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(account.getHost());
        mailSender.setPort(account.getPort());
        mailSender.setUsername(account.getUsername());
        mailSender.setPassword(account.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.putAll(account.getProperties());
        return mailSender;
    }

    private boolean hasRemainingQuota(String username, Map<String, EmailAccountUsage> usageMap) {
        return usageMap.getOrDefault(username, new EmailAccountUsage()).getEmailCount() < mailProperties.getDailyLimit();
    }

    private void updateEmailUsage(String username) {
        EmailAccountUsage usage = emailAccountUsageRepository.findByUsername(username).orElseGet(() -> {
            EmailAccountUsage newUsage = new EmailAccountUsage();
            newUsage.setUsername(username);
            newUsage.setEmailCount(0);
            newUsage.setLastResetDate(LocalDate.now());
            return newUsage;
        });

        usage.setEmailCount(usage.getEmailCount() + 1);
        usage.setLastSent(LocalDateTime.now());
        emailAccountUsageRepository.save(usage);
    }
}
