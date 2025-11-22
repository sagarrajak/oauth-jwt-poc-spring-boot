package com.debaterr.app.authresouce.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        // Placeholder implementation
        // In a real application, integrate with an email service like:
        // - JavaMailSender
        // - SendGrid
        // - Amazon SES
        // - etc.

        System.out.println("=== EMAIL PLACEHOLDER ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("=== END EMAIL ===");

        // TODO: Implement actual email sending logic
        // Example with JavaMailSender:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(to);
        // message.setSubject(subject);
        // message.setText(body);
        // javaMailSender.send(message);
    }
}