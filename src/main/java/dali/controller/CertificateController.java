// src/main/java/dali/controller/CertificateController.java
package dali.controller;

import dali.model.Certificate;
import dali.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
// @CrossOrigin(origins = "http://localhost:4200") // ❌ remove; global CORS already allows localhost + ngrok
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    /** Get all certificates (secured by SecurityConfig: /certificates/** is authenticated) */
    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    /** Get a certificate by ID */
    @GetMapping("/{id}")
    public Certificate getCertificate(@PathVariable Long id) {
        return certificateService.getCertificateById(id);
    }
}
