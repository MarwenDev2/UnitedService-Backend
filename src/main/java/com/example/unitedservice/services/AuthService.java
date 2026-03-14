package com.example.unitedservice.services;


import com.example.unitedservice.entities.User;
import com.example.unitedservice.repositories.UserRepository;
import com.example.unitedservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public String loginUser(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            System.out.println("User found: " + user.getEmail());
            System.out.println("Password matches: " + passwordEncoder.matches(password, user.getPassword()));
            System.out.println("User authorities: " + user.getAuthorities());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String authenticatedEmail = authentication.getName();
            User authenticatedUser = userRepository.findByEmail(authenticatedEmail);

            return jwtUtil.generateToken(authenticatedUser);
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid email or password");
        }
    }

    public boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email);
            return jwtUtil.validateToken(token, user);
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshToken(String oldToken) {
        String username = jwtUtil.extractUsername(oldToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Use injected UserDetailsService

        if (!jwtUtil.validateToken(oldToken, userDetails)) {
            throw new RuntimeException("Invalid token");
        }

        return jwtUtil.generateToken(userDetails);
    }

}
