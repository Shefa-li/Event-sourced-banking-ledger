package com.ledger.bank_ledger.api;
import com.ledger.bank_ledger.api.dto.AuthResponse;
import com.ledger.bank_ledger.api.dto.LoginRequest;
import com.ledger.bank_ledger.api.dto.RegisterRequest;
import com.ledger.bank_ledger.domain.User;
import com.ledger.bank_ledger.repository.UserRepository;
import com.ledger.bank_ledger.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent())
            throw new IllegalArgumentException("Username already exists");

        userRepository.save(User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build());

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new IllegalArgumentException("Invalid credentials");

        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(request.username())));
    }
}