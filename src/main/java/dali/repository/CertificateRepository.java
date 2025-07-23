package dali.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dali.model.Certificate;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByUserEmail(String email); // ✅ MUST be here
}
