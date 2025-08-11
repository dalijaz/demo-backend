// src/main/java/dali/controller/AuthController.java
package dali.controller;

import dali.model.User;
import dali.security.JwtUtil;
import dali.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://956105106b9e.ngrok-free.app"   // <-- your current ngrok FRONTEND URL
})
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
            userService.registerUser(user); // sends email with clickable verify link
            return ResponseEntity.ok("Signup successful! Please check your email to verify your account.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Signup failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        System.out.println("🔐 Login attempt for: " + loginUser.getEmail());

        Optional<User> userOpt = userService.authenticate(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Invalid credentials or account not verified");
        }

        User user = userOpt.get();

        // strip ROLE_ for frontend and token
        String roleNoPrefix = user.getRole().name().replaceFirst("^ROLE_", "");
        String token = jwtUtil.generateToken(user.getEmail(), roleNoPrefix);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", roleNoPrefix,
                "email", user.getEmail()
        ));
    }

    /** User clicks: GET /auth/verify?token=...  */
    @GetMapping("/verify")
    public void verifyAccount(@RequestParam String token, HttpServletResponse resp) throws IOException {
        boolean verified = userService.verifyUser(token);
        String redirect = userService.getFrontendBase()
                + "/verify-account?status=" + (verified ? "success" : "failed");
        resp.sendRedirect(redirect);
    }
}
