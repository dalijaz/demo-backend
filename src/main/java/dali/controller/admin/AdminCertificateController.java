// src/main/java/dali/controller/AdminCertificateController.java
package dali.controller.admin;

import dali.model.Certificate;
import dali.repository.CertificateRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/certificates")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://*.ngrok-free.app"
})
public class AdminCertificateController {

    private final CertificateRepository repo;

    public AdminCertificateController(CertificateRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Certificate> all() {
        return repo.findAll();
    }

    @PostMapping
    public Certificate create(@RequestBody Certificate c) {
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public Certificate update(@PathVariable Long id, @RequestBody Certificate c) {
        Certificate existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

        // ✅ partial update: only overwrite provided fields
        if (c.getName() != null && !c.getName().isBlank()) {
            existing.setName(c.getName());
        }
        if (c.getDescription() != null) {
            existing.setDescription(c.getDescription());
        }
        if (c.getDurationSeconds() != null) {
            existing.setDurationSeconds(c.getDurationSeconds());
        }

        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
