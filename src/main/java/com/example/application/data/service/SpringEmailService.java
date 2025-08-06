package com.example.application.data.service;/*
Author: Azarya Silaen
 */

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class SpringEmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Constructor to inject JavaMailSender dependency
    public SpringEmailService (JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Method to send email asynchronously
    @Async
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        // Create a MimeMessage object
        MimeMessage message = mailSender.createMimeMessage();

        // Create a MimeMessageHelper to assist with MimeMessage configuration
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set email properties: recipient, sender, subject, body
        helper.setTo(to);
        helper.setFrom("test@gmail.com");
        helper.setSubject(subject);
        helper.setText(body);

        // Send the email message
        mailSender.send(message);
    }

    /**
     * Sends an email message with no attachments.
     *
     * @param from       Email address from which the message will be sent.
     * @param recipients The recipients of the message.
     * @param subject    Subject header field.
     * @param text       Content of the message.
     * @throws MessagingException If an error occurs during the email sending process.
     * @throws IOException        If an I/O error occurs when loading email configuration properties.
     * @throws MessagingException If an error occurs while creating the MimeMessage.
     */


   /* public static void send(String from, String recipients, String subject, String text)
            throws MessagingException, IOException {
        // Check for null references
        Objects.requireNonNull(from);
        Objects.requireNonNull(recipients);

        // Load email configuration from properties file
        Properties properties = new Properties();
        properties.load(SpringEmailService.class.getResourceAsStream("/mail.properties"));
        String username = properties.getProperty("mail.smtp.username");
        String password = properties.getProperty("mail.smtp.password");

        // Configure the connection to the SMTP server
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(properties);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Create and configure the MimeMessage
        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.addTo(recipients);

         *//*
        String emailHost = "smtp.gmail.com";
        String fromUser = "emailid";//just the id alone without @gmail.com
        String fromUserEmailPassword = "test";
        Session session = Session.getInstance(properties);

        transport.connect(emailHost, fromUser, fromUserEmailPassword);
        transport.sendMessage(helper.getMimeMessage(), helper.getMimeMessage().getAllRecipients());
        transport.close();
         *//*

        // Send the email message
        mailSender.send(message);
    }*/


}
