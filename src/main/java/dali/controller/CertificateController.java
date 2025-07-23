package dali.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import dali.service.CertificateService;
import dali.model.Certificate;

import java.util.List;

@RestController
@RequestMapping("/certificates")
@CrossOrigin(origins = "http://localhost:4200")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    // Only returns certificates for the authenticated user (NO email param needed)
    @GetMapping
    public List<Certificate> getUserCertificates(Authentication authentication) {
        String email = authentication.getName(); // JWT subject
        return certificateService.getCertificatesByEmail(email);
    }

    @GetMapping("/{id}")
    public Certificate getCertificate(@PathVariable Long id) {
        return certificateService.getCertificateById(id);
    }

    @PostMapping
    public Certificate createCertificate(@RequestBody Certificate certificate) {
        return certificateService.saveCertificate(certificate);
    }
}
