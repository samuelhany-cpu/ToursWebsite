package com.naghamtours.service.impl;

import com.naghamtours.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body, String attachmentName, byte[] attachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            
            // Add attachment
            ByteArrayResource resource = new ByteArrayResource(attachment);
            helper.addAttachment(attachmentName, resource);
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }

    @Override
    public void sendResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request";
        String body = "To reset your password, click the following link:\n\n" +
                     "http://localhost:8080/reset-password?token=" + resetToken + "\n\n" +
                     "This link will expire in 24 hours.\n\n" +
                     "If you did not request a password reset, please ignore this email.";
        
        sendEmail(to, subject, body);
    }
} 