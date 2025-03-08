package com.ahnis.journalai.user.service;

import com.ahnis.journalai.user.entity.PasswordResetToken;
import com.ahnis.journalai.user.entity.User;
import com.ahnis.journalai.user.repository.PasswordResetTokenRepository;
import com.ahnis.journalai.user.repository.UserRepository;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${email.sendgrid.config.apiKey}")
    private String sendGridApiKey;

    @Value("${email.sendgrid.config.fromEmail}")
    private String fromEmail;

    // Generate a token and send an email
    public void sendPasswordResetEmail(String userEmail) {
        User user = userRepository.findByUsernameOrEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate a token
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS); // Token expires in 24 hours

        // Save the token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .expiryDate(expiryDate)
                .build();
        passwordResetTokenRepository.save(resetToken);

        // Send the email
        sendEmail(user.getEmail(), token);
    }

    // Send the email using SendGrid
    private void sendEmail(String toEmail, String token) {
        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Password Reset Request";

        // Create the Thymeleaf context and set variables
        Context context = new Context();
        context.setVariable("resetUrl", baseUrl + "api/v1/auth/reset-password?token=" + token);

        // Process the Thymeleaf template
        String htmlContent = templateEngine.process("password-reset-email", context);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Email sent. Status: " + response.getStatusCode());
        } catch (IOException ex) {
            log.info("Exception occurred while sending token email", ex);
        }
    }

    // Validate the token
    public boolean validateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found"));
        return resetToken != null && !resetToken.getExpiryDate().isBefore(Instant.now()); // Token is invalid or expired
    }

    // Reset the password
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found"));
        if (resetToken == null || resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Invalid or expired token");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update the password

        user.setPassword(passwordEncoder.encode(newPassword)); // Ensure you hash the password before saving
        userRepository.save(user);

        // Delete the token
        passwordResetTokenRepository.delete(resetToken);
    }
}
