package com.sunrise.dental.controller;

import com.sunrise.dental.dto.Requests.Login;
import com.sunrise.dental.dto.Requests.Signup;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.repository.UserRepository;
import com.sunrise.dental.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtService jwt;

    public AuthController(UserRepository u, PasswordEncoder p, JwtService j) {
        users = u;
        passwords = p;
        jwt = j;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody Login x) {
        var u = users.findByUsername(x.username())
                .filter(v -> v.isEnabled() && passwords.matches(x.password(), v.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Incorrect username or password."));
        return Map.of("token", jwt.create(u.getUsername(), u.getRole().name()), "username", u.getUsername(), "fullName",
                u.getFullName(), "role", u.getRole());
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@Valid @RequestBody Signup x) {
        if (users.findByUsername(x.username()).isPresent())
            throw new IllegalArgumentException("Username already exists.");
        User.Role role;
        try {
            role = User.Role.valueOf(x.role().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role must be one of ADMIN, RECEPTIONIST, or DENTIST.");
        }
        var u = new User();
        u.setUsername(x.username().trim());
        u.setFullName(x.fullName().trim());
        u.setRole(role);
        u.setPassword(passwords.encode(x.password()));
        u.setEnabled(true);
        users.save(u);
        return Map.of("token", jwt.create(u.getUsername(), u.getRole().name()), "username", u.getUsername(), "fullName",
                u.getFullName(), "role", u.getRole());
    }
}
