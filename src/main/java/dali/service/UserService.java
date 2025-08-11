package dali.service;

import dali.model.Role;
import dali.model.User;
import dali.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    // ✅ read from application.properties
    @Value("${app.backend-base}")
    private String backendBaseUrl;     // used to build the email link (hits backend)

    @Value("${app.frontend-base}")
    private String frontendBaseUrl;    // used for redirect after verification

    @Autowired
    public UserService(UserRepository userRepository,
                       JavaMailSender mailSender,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /** Exposed for controllers to build redirects post-verification */
    public String getFrontendBase() {
        return frontendBaseUrl;
    }

    /** Check if a user with the given email already exists (case-insensitive). */
    public boolean userExists(String email) {
        String normalized = normalizeEmail(email);
        return userRepository.findByEmail(normalized).isPresent();
    }

    /** Register a new user with default ROLE_USER and email verification. */
    public User registerUser(User user) {
        user.setEmail(normalizeEmail(user.getEmail()));
        if (user.getRole() == null) user.setRole(Role.ROLE_USER);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false); // require verification

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        User savedUser = userRepository.save(user);

        try {
            sendVerificationEmail(savedUser.getEmail(), token); // uses backendBaseUrl
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }

        return savedUser;
    }

    /** Authenticate by email + raw password, requires enabled=true. */
    public Optional<User> authenticate(String email, String rawPassword) {
        String normalized = normalizeEmail(email);
        Optional<User> userOpt = userRepository.findByEmail(normalized);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) return Optional.empty();
        if (!user.isEnabled()) return Optional.empty();

        return Optional.of(user);
    }

    /** Verify email using token. */
    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);
            user.setVerificationToken(null); // consume token
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /** Optional: promote an existing user to admin. */
    public boolean promoteToAdmin(String email) {
        String normalized = normalizeEmail(email);
        return userRepository.findByEmail(normalized).map(u -> {
            u.setRole(Role.ROLE_ADMIN);
            userRepository.save(u);
            return true;
        }).orElse(false);
    }

    // ---------------- private helpers ----------------

    /** Sends an email with a BACKEND link: user clicks → backend verifies → redirects to Angular. */
    private void sendVerificationEmail(String toEmail, String token) {
        String verifyUrl = backendBaseUrl + "/auth/verify?token=" + token;
        System.out.println("🔐 Verification link (email): " + verifyUrl);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your email");
        message.setText("""
                Welcome! Please click the link below to verify your email:
                %s

                If you didn't create this account, you can ignore this email.
                """.formatted(verifyUrl));
        mailSender.send(message);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
