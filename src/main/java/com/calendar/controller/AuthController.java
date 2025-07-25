package com.calendar.controller;

import com.calendar.dto.AuthResponse;
import com.calendar.dto.ErrorResponse;
import com.calendar.dto.LoginRequest;
import com.calendar.dto.RegisterRequest;
import com.calendar.exception.EmailAlreadyExistsException;
import com.calendar.exception.InvalidCredentialsException;
import com.calendar.exception.UserNotFoundException;
import com.calendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String token = authService.register(request.getEmail(), request.getPassword(),
                                              request.getFirstName(), request.getLastName());
            
            return ResponseEntity.ok(new AuthResponse(token, "User registered successfully"));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

