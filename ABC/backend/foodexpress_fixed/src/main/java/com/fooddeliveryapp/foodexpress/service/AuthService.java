package com.fooddeliveryapp.foodexpress.service;

import com.fooddeliveryapp.foodexpress.dto.AuthRequest;
import com.fooddeliveryapp.foodexpress.dto.AuthResponse;
import com.fooddeliveryapp.foodexpress.dto.RegisterRequest;
import com.fooddeliveryapp.foodexpress.entity.DeliveryAgent;
import com.fooddeliveryapp.foodexpress.entity.Role;
import com.fooddeliveryapp.foodexpress.entity.User;
import com.fooddeliveryapp.foodexpress.repository.DeliveryAgentRepository;
import com.fooddeliveryapp.foodexpress.repository.UserRepository;
import com.fooddeliveryapp.foodexpress.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final DeliveryAgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user);

        // If registered as Agent, create Agent profile
        if (request.getRole() == Role.AGENT) {
            var agent = DeliveryAgent.builder()
                    .user(user)
                    .status("AVAILABLE")
                    .totalEarnings(0.0)
                    .build();
            agentRepository.save(agent);
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }
}
