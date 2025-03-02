package com.ahnis.journalai.notification;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SendGridEmailService {
    @Value("${email.SENDGRID_API_KEY}")
    private String sendGridApiKey;

    public void sendEmail(String toEmail, String subject, String content) {
        Email from = new Email("ahnisaneja@gmail.com");
        Email to = new Email(toEmail);
        Content emailContent = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, to, emailContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        var request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("Email sent to {} with status code: {}", toEmail, response.getStatusCode());
        } catch (IOException e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}
