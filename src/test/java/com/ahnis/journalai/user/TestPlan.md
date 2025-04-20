# User Module Test Plan

## Completed Tests

### Service Layer Tests
- [x] UserServiceImplTest
- [x] AuthServiceImplTest
- [x] AdminServiceImplTest
- [x] TherapistServiceTest
- [x] CustomUserDetailsServiceTest

### Repository Layer Tests
- [x] UserRepositoryTest
- [x] TherapistRepositoryTest
- [x] PasswordResetTokenRepositoryTest

### Scheduler Tests
- [x] JournalingReminderSchedulerTest

## Planned Tests

### Controller Layer Tests
These tests should use MockMvc to test the REST endpoints.

#### AdminController Tests
- [ ] Test getAllUsers endpoint
- [ ] Test getUserById endpoint
- [ ] Test updateUser endpoint
- [ ] Test deleteUser endpoint
- [ ] Test enableUser endpoint
- [ ] Test disableUser endpoint
- [ ] Test lockUser endpoint
- [ ] Test unlockUser endpoint
- [ ] Test registerMultipleUsers endpoint

#### AllTherapistController Tests
- [ ] Test getAllTherapists endpoint
- [ ] Test searchTherapists endpoint

#### AuthController Tests
- [ ] Test registerUser endpoint
- [ ] Test loginUser endpoint
- [ ] Test registerTherapist endpoint
- [ ] Test requestPasswordReset endpoint
- [ ] Test resetPassword endpoint

#### TherapistManagementController Tests
- [ ] Test getProfile endpoint
- [ ] Test updateProfile endpoint
- [ ] Test getClients endpoint

#### UserController Tests
- [ ] Test getCurrentUser endpoint
- [ ] Test updateCurrentUser endpoint
- [ ] Test deleteCurrentUser endpoint
- [ ] Test updatePreferences endpoint
- [ ] Test getSubscribedTherapist endpoint
- [ ] Test subscribeToTherapist endpoint

### End-to-End Tests
These tests should test complete user flows from end to end.

#### User Registration and Authentication Flow
- [ ] Test user registration, login, and profile update
- [ ] Test password reset flow

#### Therapist Registration and Management Flow
- [ ] Test therapist registration, login, and profile update
- [ ] Test therapist client management

#### User-Therapist Interaction Flow
- [ ] Test user subscribing to a therapist
- [ ] Test user viewing therapist profile

#### Admin Management Flow
- [ ] Test admin user management (enable, disable, lock, unlock)
- [ ] Test admin bulk user registration

## Next Steps for Journal Module

After completing the user module tests, we should move on to the journal module tests:

1. Identify the components of the journal module
2. Create a test plan for the journal module
3. Implement service layer tests
4. Implement repository layer tests
5. Implement controller layer tests
6. Implement end-to-end tests

## Next Steps for Analysis Module

After completing the journal module tests, we should move on to the analysis module tests:

1. Identify the components of the analysis module
2. Create a test plan for the analysis module
3. Implement service layer tests
4. Implement repository layer tests
5. Implement controller layer tests
6. Implement end-to-end tests

## Next Steps for Notification Module

After completing the analysis module tests, we should move on to the notification module tests:

1. Identify the components of the notification module
2. Create a test plan for the notification module
3. Implement service layer tests
4. Implement repository layer tests
5. Implement controller layer tests
6. Implement end-to-end tests

## Next Steps for Chatbot Module

After completing the notification module tests, we should move on to the chatbot module tests:

1. Identify the components of the chatbot module
2. Create a test plan for the chatbot module
3. Implement service layer tests
4. Implement repository layer tests
5. Implement controller layer tests
6. Implement end-to-end tests
