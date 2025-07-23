package dali.service;

import dali.model.Certificate;
import dali.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> getCertificatesByEmail(String email) {
        System.out.println("🔍 Looking for certificates for user email: " + email);

        List<Certificate> certs = certificateRepository.findByUserEmail(email);

        System.out.println("📦 Certificates found: " + certs.size());
        for (Certificate cert : certs) {
            System.out.println("   - " + cert.getName() + ": " + cert.getDescription());
        }

        return certs;
    }

    public Certificate getCertificateById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    public Certificate saveCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }
}
