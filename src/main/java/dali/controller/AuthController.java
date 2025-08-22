// src/main/java/dali/controller/AuthController.java
package dali.controller;

import dali.model.User;
import dali.model.Role; // <-- make sure this enum exists: ROLE_ADMIN, ROLE_USER
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
        "https://956105106b9e.ngrok-free.app"
})
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userService.userExists(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Signup successful! Please check your email to verify your account.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Signup failed: " + e.getMessage());
        }
    }

    /** USER-ONLY login: rejects admin accounts */
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody User loginUser) {
        Optional<User> userOpt = userService.authenticate(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Invalid credentials or account not verified");
        }
        User user = userOpt.get();

        if (user.getRole() != Role.ROLE_USER) {
            // prevent admins from logging in via the user endpoint
            return ResponseEntity.status(403).body("This endpoint is for USER accounts only.");
        }

        String roleWithPrefix = user.getRole().name();                      // "ROLE_USER"
        String roleNoPrefix   = roleWithPrefix.replaceFirst("^ROLE_", "");  // "USER"
        String token          = jwtUtil.generateToken(user.getEmail(), roleWithPrefix);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role",  roleNoPrefix,
                "email", user.getEmail()
        ));
    }

    /** ADMIN-ONLY login: rejects user accounts */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody User loginUser) {
        Optional<User> userOpt = userService.authenticate(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Invalid credentials or account not verified");
        }
        User user = userOpt.get();

        if (user.getRole() != Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body("This endpoint is for ADMIN accounts only.");
        }

        String roleWithPrefix = user.getRole().name();                     // "ROLE_ADMIN"
        String roleNoPrefix   = "ADMIN";
        String token          = jwtUtil.generateToken(user.getEmail(), roleWithPrefix);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role",  roleNoPrefix,
                "email", user.getEmail()
        ));
    }

    /** Email verification redirect */
    @GetMapping("/verify")
    public void verifyAccount(@RequestParam String token, HttpServletResponse resp) throws IOException {
        boolean verified = userService.verifyUser(token);
        String redirect = userService.getFrontendBase()
                + "/verify-account?status=" + (verified ? "success" : "failed");
        resp.sendRedirect(redirect);
    }
}
