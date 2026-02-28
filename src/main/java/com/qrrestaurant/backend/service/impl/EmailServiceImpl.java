package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.service.EmailService;
//ADD THESE IMPORTS at the top
import com.qrrestaurant.backend.entity.Restaurant;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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
    @Override
    public void sendRestaurantRegistrationConfirmation(String to, String restaurantName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("✅ Restaurant Registration Received");
            String emailBody = String.format(
                "Dear Restaurant Owner,\n\n" +
                "Thank you for registering '%s' with QR Restaurant Platform.\n\n" +
                "Your application is currently under review. You will receive another email once it is approved.\n\n" +
                "Regards,\nQR Restaurant Platform Team",
                restaurantName
            );
            message.setText(emailBody);
            mailSender.send(message);
            // you might want to log success
        } catch (MailException e) {
            // log error
        }
    }
    
   
        
        
        @Override
        public void sendApprovalEmail(Restaurant restaurant, String rawPassword) {
            String subject = "🎉 Your Restaurant Has Been Approved – Login Credentials Inside";
            
            // You need to inject frontendUrl, e.g.:
            // @Value("${app.frontend.url}")
            // private String frontendUrl;
            String loginUrl = frontendUrl + "/owner/login";
            
            String content = "<html><body style='font-family: Arial;'>"
                + "<h2 style='color: #4CAF50;'>Congratulations " + restaurant.getOwnerName() + "!</h2>"
                + "<p>Your restaurant <strong>" + restaurant.getName() + "</strong> has been approved.</p>"
                + "<p>You can now log in to the owner portal using the following credentials:</p>"
                + "<p><strong>Username:</strong> " + restaurant.getEmail() + "<br>"
                + "<strong>Password:</strong> " + rawPassword + "</p>"
                + "<p><strong>Login URL:</strong> <a href='" + loginUrl + "'>" + loginUrl + "</a></p>"
                + "<p>Please change your password after first login for security.</p>"
                + "<br>"
                + "<p>Thanks,<br>QR Restaurant Team</p>"
                + "</body></html>";
         
            
            sendEmail(restaurant.getEmail(), subject, content);
        }
        @Override
        public void sendRejectionEmail(Restaurant restaurant, String reason) {
            String subject = "Update on Your Restaurant Application";
            
            String reasonText = (reason != null && !reason.isEmpty()) 
                ? "<p><strong>Reason:</strong> " + reason + "</p>"
                : "<p>Please contact support for more information.</p>";
            
            String content = "<html><body style='font-family: Arial;'>"
                + "<h2 style='color: #f44336;'>Application Update</h2>"
                + "<p>Dear " + restaurant.getOwnerName() + ",</p>"
                + "<p>Your restaurant <strong>" + restaurant.getName() + "</strong> has not been approved at this time.</p>"
                + reasonText
                + "<br>"
                + "<p>You can update your information and reapply.</p>"
                + "<p>Thanks,<br>QR Restaurant Team</p>"
                + "</body></html>";
            
            sendEmail(restaurant.getEmail(), subject, content);
        }
        
        private void sendEmail(String to, String subject, String htmlContent) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true); // true means HTML
                
                mailSender.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send email", e);
            }
        }

        @Override
        @Async
        public void sendSuspensionEmail(Restaurant restaurant, String reason) {
            String subject = "⚠️ Important: Your Restaurant Has Been Suspended";
            
            String reasonText = (reason != null && !reason.isEmpty()) 
                ? "<p><strong>Reason:</strong> " + reason + "</p>"
                : "<p>No specific reason provided.</p>";
            
            String content = "<html><body style='font-family: Arial; max-width: 600px; margin: 0 auto;'>"
                + "<div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px;'>"
                + "<h2 style='color: #856404; margin-top: 0;'>Restaurant Suspension Notice</h2>"
                + "</div>"
                
                + "<div style='padding: 20px;'>"
                + "<p>Dear <strong>" + restaurant.getOwnerName() + "</strong>,</p>"
                
                + "<p>We regret to inform you that your restaurant <strong>" + restaurant.getName() + "</strong> "
                + "has been <span style='color: #dc3545; font-weight: bold;'>suspended</span> from the QR Restaurant Platform.</p>"
                
                + "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
                + "<h3 style='margin-top: 0; color: #495057;'>Suspension Details:</h3>"
                + reasonText
                + "</div>"
                
                + "<p><strong>What this means:</strong></p>"
                + "<ul style='color: #6c757d;'>"
                + "<li>Your restaurant will not appear in customer searches</li>"
                + "<li>You cannot accept new orders at this time</li>"
                + "<li>Your dashboard access is limited</li>"
                + "</ul>"
                
                + "<p><strong>Next Steps:</strong></p>"
                + "<ol style='color: #6c757d;'>"
                + "<li>Review the suspension reason above</li>"
                + "<li>Make necessary corrections or contact support</li>"
                + "<li>Once resolved, we can reinstate your account</li>"
                + "</ol>"
                
                + "<div style='background-color: #e7f3ff; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
                + "<p style='margin: 0;'><strong>Need help?</strong> Contact our support team at support@qrrestaurant.com</p>"
                + "</div>"
                
                + "<p>Regards,<br>"
                + "<strong>QR Restaurant Platform Team</strong></p>"
                + "</div>"
                
                + "<div style='background-color: #f1f1f1; padding: 15px; text-align: center; font-size: 12px; color: #666;'>"
                + "<p>This is an automated message, please do not reply directly.</p>"
                + "</div>"
                + "</body></html>";
            
            try {
                sendEmail(restaurant.getEmail(), subject, content);
                log.info("✅ Suspension email sent to: {}", restaurant.getEmail());
            } catch (Exception e) {
                log.error("❌ Failed to send suspension email to {}: {}", restaurant.getEmail(), e.getMessage());
            }
        }

        @Override
        @Async
        public void sendReopenEmail(Restaurant restaurant) {
            String subject = "✅ Good News: Your Restaurant Has Been Reinstated";
            
            String content = "<html><body style='font-family: Arial; max-width: 600px; margin: 0 auto;'>"
                + "<div style='background-color: #d4edda; border-left: 4px solid #28a745; padding: 20px;'>"
                + "<h2 style='color: #155724; margin-top: 0;'>🎉 Restaurant Reinstated Successfully!</h2>"
                + "</div>"
                
                + "<div style='padding: 20px;'>"
                + "<p>Dear <strong>" + restaurant.getOwnerName() + "</strong>,</p>"
                
                + "<p>Great news! Your restaurant <strong>" + restaurant.getName() + "</strong> "
                + "has been <span style='color: #28a745; font-weight: bold;'>reinstated</span> and is now "
                + "<span style='color: #28a745; font-weight: bold;'>ACTIVE</span> on the QR Restaurant Platform.</p>"
                
                + "<div style='background-color: #e8f5e9; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
                + "<h3 style='margin-top: 0; color: #2e7d32;'>What happens now?</h3>"
                + "<ul style='color: #2e7d32;'>"
                + "<li>✅ Your restaurant is visible to customers again</li>"
                + "<li>✅ You can accept new orders immediately</li>"
                + "<li>✅ Full dashboard access restored</li>"
                + "</ul>"
                + "</div>"
                
                + "<div style='background-color: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0;'>"
                + "<p style='margin: 0; color: #856404;'>"
                + "<strong>💡 Tip:</strong> Make sure to maintain compliance to avoid future suspensions.</p>"
                + "</div>"
                
                + "<p>We're glad to have you back! If you have any questions, we're here to help.</p>"
                
                + "<p>Regards,<br>"
                + "<strong>QR Restaurant Platform Team</strong></p>"
                + "</div>"
                
                + "<div style='background-color: #f1f1f1; padding: 15px; text-align: center; font-size: 12px; color: #666;'>"
                + "<p>This is an automated message, please do not reply directly.</p>"
                + "</div>"
                + "</body></html>";
            
            try {
                sendEmail(restaurant.getEmail(), subject, content);
                log.info("✅ Reopen email sent to: {}", restaurant.getEmail());
            } catch (Exception e) {
                log.error("❌ Failed to send reopen email to {}: {}", restaurant.getEmail(), e.getMessage());
            }
        }
        
       
        
        
        
        
        
}