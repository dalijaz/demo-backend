package dali.controller;

import dali.model.User;
import dali.security.JwtUtil;
import dali.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
public ResponseEntity<String> signup(@RequestBody User user) {
    if (userService.userExists(user.getEmail())) {
        return ResponseEntity.badRequest().body("Email already registered");
    }

    try {
        userService.registerUser(user);
        return ResponseEntity.ok("Signup successful! Please check your email to verify your account.");
    } catch (Exception e) {
        e.printStackTrace(); // For better logging
        return ResponseEntity.internalServerError().body("Signup failed: " + e.getMessage());
    }
}

   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User loginUser) {
    System.out.println("🔐 Login attempt for: " + loginUser.getEmail());
    System.out.println("🔑 Raw password received: " + loginUser.getPassword());

    Optional<User> userOpt = userService.authenticate(loginUser.getEmail(), loginUser.getPassword());

    if (userOpt.isEmpty()) {
        return ResponseEntity.status(403).body("Invalid credentials or account not verified");
    }

    String token = jwtUtil.generateToken(loginUser.getEmail());
    return ResponseEntity.ok().body(Map.of("token", token));
}



    // ✅ Verify
    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            return ResponseEntity.ok("Your account has been verified!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }
    }
}
