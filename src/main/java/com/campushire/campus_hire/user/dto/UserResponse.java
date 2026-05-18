package com.campushire.campus_hire.user.dto;

import com.campushire.campus_hire.user.internal.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role
) {
}
