package com.campushire.campus_hire.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email cannot be Empty")
        @Email(message = "Invalid Email format")
        String email,


        @NotBlank(message = "Password cannot be Empty")
        String password
){ }
