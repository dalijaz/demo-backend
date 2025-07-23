package dali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dali.model.Certificate;
import dali.model.User;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByUser(User user);
}
