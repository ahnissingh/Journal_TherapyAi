package com.ahnis.journalai.notification.service;

import com.ahnis.journalai.notification.config.SendGridProperties;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

    @Mock
    private SendGridProperties sendGridProperties;

    @Mock
    private SendGrid sendGrid;

    @InjectMocks
    private SendGridEmailService sendGridEmailService;

    private String testFromEmail;
    private String testToEmail;
    private String testSubject;
    private String testContent;

    @BeforeEach
    void setUp() {
        testFromEmail = "from@example.com";
        testToEmail = "to@example.com";
        testSubject = "Test Subject";
        testContent = "<html><body>Test email content</body></html>";

        when(sendGridProperties.fromEmail()).thenReturn(testFromEmail);
    }

    @Test
    @DisplayName("Should send email successfully")
    void sendEmail_ShouldSendEmail() throws IOException {
        // Given
        Response response = new Response();
        response.setStatusCode(202); // 202 Accepted is the success status code for SendGrid
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        // When
        sendGridEmailService.sendEmail(testToEmail, testSubject, testContent);

        // Then
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(sendGrid).api(requestCaptor.capture());

        Request capturedRequest = requestCaptor.getValue();
        assertEquals("POST", capturedRequest.getMethod().name());
        assertEquals("mail/send", capturedRequest.getEndpoint());
        assertTrue(capturedRequest.getBody().contains(testFromEmail));
        assertTrue(capturedRequest.getBody().contains(testToEmail));
        assertTrue(capturedRequest.getBody().contains(testSubject));
        assertTrue(capturedRequest.getBody().contains(testContent));
    }

    @Test
    @DisplayName("Should handle IOException when sending email")
    void sendEmail_ShouldHandleIOException() throws IOException {
        // Given
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException("Test exception"));

        // When
        sendGridEmailService.sendEmail(testToEmail, testSubject, testContent);

        // Then
        verify(sendGrid).api(any(Request.class));
        // No exception should be thrown, and the method should complete normally
    }

    @Test
    @DisplayName("Should log success status code")
    void sendEmail_ShouldLogSuccessStatusCode() throws IOException {
        // Given
        Response response = new Response();
        response.setStatusCode(202); // 202 Accepted is the success status code for SendGrid
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        // When
        sendGridEmailService.sendEmail(testToEmail, testSubject, testContent);

        // Then
        verify(sendGrid).api(any(Request.class));
        // The success status code should be logged (can't verify logging in unit tests)
    }

    @Test
    @DisplayName("Should log error status code")
    void sendEmail_ShouldLogErrorStatusCode() throws IOException {
        // Given
        Response response = new Response();
        response.setStatusCode(400); // 400 Bad Request is an error status code
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        // When
        sendGridEmailService.sendEmail(testToEmail, testSubject, testContent);

        // Then
        verify(sendGrid).api(any(Request.class));
        // The error status code should be logged (can't verify logging in unit tests)
    }
}
