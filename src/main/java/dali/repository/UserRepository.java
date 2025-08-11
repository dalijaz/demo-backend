package dali.repository;

import dali.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Existing
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);

    // Useful helpers
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByEmailAndEnabledTrue(String email);
}
