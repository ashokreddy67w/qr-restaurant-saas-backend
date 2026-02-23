package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    // ✅ EXPLICIT CONSTRUCTOR for final field
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    @Async
    public void sendVerificationEmail(String to, String verificationLink, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("QR Restaurant - Verify Your Email Address");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Thank you for registering with QR Restaurant Platform!\n\n" +
                "Please verify your email address by clicking the link below:\n\n" +
                "%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not create this account, please ignore this email.\n\n" +
                "Regards,\n" +
                "QR Restaurant Platform Team",
                name, verificationLink
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            log.info("✅ Verification email sent to: {}", to);
        } catch (MailException e) {
            log.error("❌ Failed to send verification email to {}: {}", to, e.getMessage());
        }
    }
    
    @Override
    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("🎉 Welcome to QR Restaurant Platform!");
            
            String loginUrl = frontendUrl + "/login";
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "🎉 WELCOME TO QR RESTAURANT PLATFORM!\n\n" +
                "Your email has been successfully verified. Your admin account is now active.\n\n" +
                "You can now log in to your dashboard at:\n%s\n\n" +
                "Here's what you can do as an admin:\n" +
                "✓ Manage restaurants\n" +
                "✓ Approve new restaurant applications\n" +
                "✓ Monitor platform activity\n" +
                "✓ View analytics and reports\n\n" +
                "If you have any questions, feel free to contact our support team.\n\n" +
                "Regards,\n" +
                "The QR Restaurant Platform Team",
                name, loginUrl
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            log.info("✅ Welcome email sent to: {}", to);
        } catch (MailException e) {
            log.error("❌ Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }
    
    @Override
    @Async
    public void sendPasswordResetOtp(String to, String otp, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("QR Restaurant - Password Reset OTP");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "We received a request to reset your password.\n\n" +
                "Your OTP is: %s\n\n" +
                "This OTP is valid for 15 minutes.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Regards,\n" +
                "QR Restaurant Platform Team",
                name, otp
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            log.info("✅ Password reset OTP email sent to: {}", to);
        } catch (MailException e) {
            log.error("❌ Failed to send OTP email to {}: {}", to, e.getMessage());
        }
    }
    
    @Override
    @Async
    public void sendPasswordResetConfirmation(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("QR Restaurant - Password Reset Successful");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your password has been successfully reset.\n\n" +
                "If you did not perform this action, please contact support immediately.\n\n" +
                "Regards,\n" +
                "QR Restaurant Platform Team",
                name
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            log.info("✅ Password reset confirmation email sent to: {}", to);
        } catch (MailException e) {
            log.error("❌ Failed to send confirmation email to {}: {}", to, e.getMessage());
        }
    }
    
    @Override
    @Async
    public void sendAccountActivationEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("QR Restaurant - Account Activated");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your QR Restaurant admin account has been activated.\n\n" +
                "You can now log in to access all features.\n\n" +
                "Regards,\n" +
                "QR Restaurant Platform Team",
                name
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            log.info("✅ Account activation email sent to: {}", to);
        } catch (MailException e) {
            log.error("❌ Failed to send activation email to {}: {}", to, e.getMessage());
        }
    }
    
    @Override
    @Async
    public void sendLoginAlert(String to, String name, String ipAddress) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("QR Restaurant - New Login Alert");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "A new login was detected on your QR Restaurant admin account.\n\n" +
                "IP Address: %s\n" +
                "Time: %s\n\n" +
                "If this was you, no action is needed.\n" +
                "If you don't recognize this activity, please change your password immediately.\n\n" +
                "Regards,\n" +
                "QR Restaurant Platform Team",
                name, ipAddress, java.time.LocalDateTime.now()
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            log.info("✅ Login alert email sent to: {}", to);
        } catch (MailException e) {
            log.error("❌ Failed to send login alert to {}: {}", to, e.getMessage());
        }
    }
}