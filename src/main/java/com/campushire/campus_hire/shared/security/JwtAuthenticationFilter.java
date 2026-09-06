package com.campushire.campus_hire.shared.security;

import com.campushire.campus_hire.shared.redis.RedisTokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final RedisTokenBlacklistService redisBlacklistService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, RedisTokenBlacklistService redisBlacklistService) {
        this.jwtTokenService = jwtTokenService;
        this.redisBlacklistService = redisBlacklistService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Look for the Authorization header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token found. Let the next filter handle it (might be a public endpoint like /register)
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the token from the header (remove "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 3. Extract data from the token
            String userEmail = jwtTokenService.extractEmail(jwt);
            String jti = jwtTokenService.extractJti(jwt); // Extract the unique token ID

            // 4. REDIS BLACKLIST CHECK (The Interview Flex)
            if (redisBlacklistService.isBlacklisted(jti)) {
                // The token is physically valid, but the user logged out. KICK THEM OUT.
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has been revoked (Logged Out)");
                return;
            }

            // 5. If token is good and not blacklisted, authenticate the user in Spring Security Context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtTokenService.isTokenValid(jwt)) {

                    // Tell Spring "This user is verified and allowed in"
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            Collections.emptyList() // We will add Roles here later
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // If the token is expired or tampered with, catch the error and reject
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or Expired JWT");
            return;
        }

        // 6. Pass the clean request to the next bouncer or the controller
        filterChain.doFilter(request, response);
    }
}