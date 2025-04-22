# Notification Module Test Documentation

## Overview
This document provides an overview of the testing approach for the Notification module in the JournalAI application. The Notification module is responsible for sending various types of email notifications to users, including mood reports, password reset emails, journal reminders, milestone notifications, and suicidal alert emails.

## Test Structure
The tests for the Notification module are organized into the following categories:

### Service Tests
- **NotificationServiceTest**: Tests the service layer that orchestrates the sending of different types of notifications.
- **SendGridEmailServiceTest**: Tests the service responsible for sending emails via the SendGrid API.

### Template Tests
- **EmailTemplateServiceTest**: Tests the service responsible for generating HTML email content using Thymeleaf templates.

## Test Approach

### Service Tests
Service tests use Mockito to mock dependencies and focus on testing the business logic. The tests verify:
- Email sending for different notification types
- Error handling
- Proper orchestration between template generation and email sending

### Template Tests
Template tests focus on verifying that the correct templates are used and that the context is properly populated. The tests verify:
- Template selection for different email types
- Context population with the correct variables
- Integration with the Thymeleaf template engine

## Best Practices
The tests follow these best practices:
- Each test method tests a single functionality
- Tests are independent and can run in any order
- Tests use descriptive names that indicate what is being tested
- Tests include both positive and negative scenarios
- Tests verify both functional requirements and error handling

## Running the Tests
To run all tests for the Notification module:
```bash
mvn test -Dtest=com.ahnis.journalai.notification.*Test
```

To run a specific test class:
```bash
mvn test -Dtest=com.ahnis.journalai.notification.service.NotificationServiceTest
mvn test -Dtest=com.ahnis.journalai.notification.service.SendGridEmailServiceTest
mvn test -Dtest=com.ahnis.journalai.notification.template.EmailTemplateServiceTest
```

## Test Coverage
The tests cover:
- All public methods in the service and template classes
- Both success and error scenarios
- Edge cases such as missing data, invalid requests, and exception handling

## Testing EmailTemplateService
The EmailTemplateService is a critical component that generates HTML content for emails using Thymeleaf templates. Testing this service requires:

1. **Mocking Dependencies**:
   - SpringTemplateEngine: Mock the template processing to return predefined HTML content
   - AppProperties: Mock configuration properties for URLs

2. **Testing Template Selection**:
   - Verify that the correct template is selected for each email type

3. **Testing Context Population**:
   - Verify that the context is populated with the correct variables for each email type

4. **Testing URL Construction**:
   - Verify that URLs are correctly constructed using the base URL and specific paths

This approach ensures that the EmailTemplateService correctly generates HTML content for all types of emails without requiring actual Thymeleaf templates to be available during testing.
