// src/main/java/dali/controller/admin/AdminCertificateController.java
package dali.controller.admin;

import dali.model.Certificate;
import dali.repository.CertificateRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/certificates")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminCertificateController {

    private final CertificateRepository repo;

    public AdminCertificateController(CertificateRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Certificate> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Certificate get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Certificate create(@RequestBody Certificate c) {
        c.setId(null);
        return repo.save(c);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Certificate update(@PathVariable Long id, @RequestBody Certificate c) {
        repo.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));
        c.setId(id);
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
