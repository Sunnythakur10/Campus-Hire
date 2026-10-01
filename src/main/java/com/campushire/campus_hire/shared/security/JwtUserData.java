package com.campushire.campus_hire.shared.security;

import java.util.UUID;

public record JwtUserData(UUID userId , String email, String role) {
}
