package com.campushire.campus_hire.user;


import com.campushire.campus_hire.shared.redis.RedisTokenBlacklistService;
import com.campushire.campus_hire.shared.security.JwtTokenService;
import com.campushire.campus_hire.user.dto.LoginRequest;
import com.campushire.campus_hire.user.dto.RegisterRequest;
import com.campushire.campus_hire.user.dto.UserResponse;
import com.campushire.campus_hire.user.internal.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtTokenService jwtTokenService;
    private final RedisTokenBlacklistService redisBlacklistService;
    public AuthController(AuthService authService,
                          JwtTokenService jwtTokenService,
                          RedisTokenBlacklistService redisBlacklistService) {
        this.authService = authService;
        this.jwtTokenService = jwtTokenService;
        this.redisBlacklistService = redisBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        UserResponse userResponse = authService.register(registerRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest){
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 1. Grab the token from the header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            // 2. Extract the unique ID and calculate how much time the token has left
            String jti = jwtTokenService.extractJti(jwt);
            long remainingTimeSeconds = jwtTokenService.getRemainingExpirationTimeMs(jwt) / 1000;

            // 3. Throw it into the Redis Jail
            if (remainingTimeSeconds > 0) {
                redisBlacklistService.blacklistToken(jti, remainingTimeSeconds);
            }
        }
        return ResponseEntity.ok("Successfully logged out. Token revoked.");
    }

}
