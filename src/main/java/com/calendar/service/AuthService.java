package com.calendar.service;

import com.calendar.config.JwtTokenProvider;
import com.calendar.entity.User;
import com.calendar.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String login(String email, String password) {
        User user = userService.findByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtTokenProvider.createToken(user.getId(), user.getEmail());
    }

    public String register(String email, String password, String firstName, String lastName) {
        User user = userService.createUser(email, password, firstName, lastName);
        return jwtTokenProvider.createToken(user.getId(), user.getEmail());
    }

    public Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}