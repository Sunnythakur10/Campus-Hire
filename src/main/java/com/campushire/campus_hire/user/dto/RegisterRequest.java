package com.campushire.campus_hire.user.dto;


/* A Data Transfer Object (DTO) is a custom object designed strictly to carry data between the client (Postman/Frontend) and your application.


* Since Java 14, we have Records.
* A RECORD is a special type of class that is deeply immutable.
* It automatically generates a constructor, getters, equals(), and hashCode().
* They are the perfect tool for DTOs.
*/

import com.campushire.campus_hire.user.internal.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Name cannot be blanked")
        String name,

        @NotBlank(message = "Email cannot be blanked")
        @Email(message = "Invalid email format")
                String email,

        @NotBlank(message = "Password cannot be blanked")
        @Pattern(regexp = "^.{8,}$" , message = "Password must be at least 8 words")
                String password,

                Role role
){ }
