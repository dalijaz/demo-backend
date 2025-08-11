package dali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dali.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    // No custom methods needed
}
