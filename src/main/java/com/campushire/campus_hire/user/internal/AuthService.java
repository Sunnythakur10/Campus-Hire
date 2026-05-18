package com.campushire.campus_hire.user.internal;

import com.campushire.campus_hire.user.dto.LoginRequest;
import com.campushire.campus_hire.user.dto.RegisterRequest;
import com.campushire.campus_hire.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public UserResponse register(RegisterRequest registerRequest){
        if(userRepository.existByEmail(registerRequest.email())){
            throw new IllegalArgumentException("Email is already Registered");
        }

        UserEntity user = new UserEntity();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        user.setRole(registerRequest.role());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));

        UserEntity savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

    }

    public String login(LoginRequest loginRequest){
        UserEntity user = userRepository.findByEmail(loginRequest.email()).orElseThrow(()->  new IllegalArgumentException("Invalid Email or Password"));

        if(!passwordEncoder.matches(loginRequest.password() , user.getPasswordHash())){
            throw new IllegalArgumentException("Invalid Email or Password");
        }

        return jwtTokenService.generateAccessToken(user);

    }

}
