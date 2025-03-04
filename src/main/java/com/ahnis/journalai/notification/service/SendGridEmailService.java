package com.ahnis.journalai.notification.service;

import com.ahnis.journalai.notification.config.SendGridProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendGridEmailService {
    //    private final SendGrid sendGrid;
    private final SendGridProperties sendGridProperties;

    public void sendEmail(String toEmail, String subject, String content) {
        var from = new Email("ahnisaneja@gmail.com");
        var to = new Email(toEmail);
        Content emailContent = new Content("text/html", content);
        Mail mail = new Mail(from, subject, to, emailContent);

        var sendGrid = new SendGrid(sendGridProperties.apiKey());
        var request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            log.info("Email sent to {} with status code: {}", toEmail, response.getStatusCode());
        } catch (IOException e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}
