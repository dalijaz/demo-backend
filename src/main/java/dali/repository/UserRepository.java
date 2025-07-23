package dali.repository;

import dali.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email (used for login/signup)
    Optional<User> findByEmail(String email);

    // Find a user by verification token (used for email confirmation)
    Optional<User> findByVerificationToken(String token);
}
