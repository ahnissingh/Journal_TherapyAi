// UserRegistrationDTO.java
package com.ahnis.journalai.dto.user.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//todo migrate validation properties to config
public record UserRegistrationRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 3, message = "Password must be at least 3 characters")
        String password,

        @Valid
        PreferencesRequest preferences
) {}
