package dali.controller;

import dali.model.Role;
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
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;

    /** Simple connectivity check (no auth, no preflight issues) */
    @GetMapping(value = "/ping", produces = "application/json")
    public Map<String, String> ping() {
        return Map.of("ok", "auth-pong");
    }

    /** Sign up a USER (public). Always returns JSON; no redirects. */
    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody User user) {
        if (userService.userExists(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }
        try {
            userService.registerUser(user); // your service also sends verification email
            return ResponseEntity.status(201).body(
                Map.of("message", "Signup successful! Please check your email to verify your account.",
                       "email", user.getEmail())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                Map.of("message", "Signup failed: " + e.getMessage())
            );
        }
    }

    /** USER login (rejects ADMIN accounts). Returns { token, role, email }. */
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> userLogin(@RequestBody User loginUser) {
        Optional<User> userOpt = userService.authenticate(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Invalid credentials or account not verified"));
        }
        User user = userOpt.get();
        if (user.getRole() != Role.ROLE_USER) {
            return ResponseEntity.status(403).body(Map.of("message", "This endpoint is for USER accounts only."));
        }

        String springRole = user.getRole().name();                 // e.g. "ROLE_USER"
        String uiRole     = springRole.replaceFirst("^ROLE_", ""); // e.g. "USER"
        String token      = jwtUtil.generateToken(user.getEmail(), springRole);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "role",  uiRole,
            "email", user.getEmail()
        ));
    }

    /** ADMIN login (rejects USER accounts). Returns { token, role: "ADMIN", email }. */
    @PostMapping(value = "/admin/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> adminLogin(@RequestBody User loginUser) {
        Optional<User> userOpt = userService.authenticate(loginUser.getEmail(), loginUser.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Invalid credentials or account not verified"));
        }
        User user = userOpt.get();
        if (user.getRole() != Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body(Map.of("message", "This endpoint is for ADMIN accounts only."));
        }

        String springRole = user.getRole().name(); // "ROLE_ADMIN"
        String token      = jwtUtil.generateToken(user.getEmail(), springRole);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "role",  "ADMIN",
            "email", user.getEmail()
        ));
    }

    /** Email verification redirects to your Angular page with a status param. */
    @GetMapping("/verify")
    public void verifyAccount(@RequestParam String token, HttpServletResponse resp) throws IOException {
        boolean verified = userService.verifyUser(token);
        String redirect = userService.getFrontendBase()
                + "/verify-account?status=" + (verified ? "success" : "failed");
        resp.sendRedirect(redirect);
    }
}
