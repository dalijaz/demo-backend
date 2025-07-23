package dali.service;

import dali.model.User;
import dali.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder; // ✅ Use the interface

    @Autowired
    public UserService(UserRepository userRepository,
                       JavaMailSender mailSender,
                       PasswordEncoder passwordEncoder) { // ✅ Inject the interface
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Checks if a user with the given email already exists.
     */
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Registers a new user by encoding the password, disabling the account,
     * generating a verification token, saving the user, and sending a verification email.
     */
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false); // Require email verification
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        User savedUser = userRepository.save(user);

        try {
            sendVerificationEmail(savedUser.getEmail(), token);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }

        return savedUser;
    }

    /**
     * Authenticates a user using email and raw password. Only allows login
     * if the account is verified (enabled).
     */
    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            System.out.println("❌ User not found: " + email);
            return Optional.empty();
        }

        if (!passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            System.out.println("❌ Password does not match for: " + email);
            return Optional.empty();
        }

        if (!user.get().isEnabled()) {
            System.out.println("❌ Account not verified for: " + email);
            return Optional.empty();
        }

        System.out.println("✅ User authenticated: " + email);
        return user;
    }

    /**
     * Sends a verification email to the user with a unique verification token.
     */
    private void sendVerificationEmail(String toEmail, String token) {
        String verifyUrl = "http://localhost:4200/verify-account?token=" + token;

        System.out.println("🔐 Verification link: " + verifyUrl);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your email");
        message.setText("Please click the following link to verify your email:\n" + verifyUrl);

        mailSender.send(message);
    }

    /**
     * Verifies a user's email using the provided token. If the token matches,
     * enables the account and clears the token.
     */
    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);
            user.setVerificationToken(null); // Invalidate token
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
