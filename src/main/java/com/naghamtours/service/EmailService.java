package com.naghamtours.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmail(String to, String subject, String body, String attachmentName, byte[] attachment);
    void sendResetEmail(String to, String resetToken);
} 