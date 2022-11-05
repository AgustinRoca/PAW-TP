package ar.edu.itba.paw.services.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailSender {
    protected static final int EMAIL_PORT = 587;
    protected static final String EMAIL_URL = "smtp.gmail.com";
    protected static final String EMAIL_USER = "medicare.paw@gmail.com";
    protected static final String EMAIL_PASSWORD = "4A%bF^Eq*jX2_c*B";

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EMAIL_URL);
        mailSender.setPort(EMAIL_PORT);
        mailSender.setUsername(EMAIL_USER);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false");
        return mailSender;
    }

    static String getEmailUser() {
        return EMAIL_USER;
    }
}
