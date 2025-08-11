package dali.controller.admin;

import dali.model.QuizQuestion;
import dali.repository.CertificateRepository;
import dali.repository.QuizQuestionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/questions")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminQuestionController {

    private final QuizQuestionRepository questions;
    private final CertificateRepository certificates;

    public AdminQuestionController(QuizQuestionRepository questions,
                                   CertificateRepository certificates) {
        this.questions = questions;
        this.certificates = certificates;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<QuizQuestion> all() {
        return questions.findAll();
    }

    @GetMapping("/by-certificate/{certId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<QuizQuestion> byCertificate(@PathVariable Long certId) {
        // ✅ stable ordering for admin UI
        return questions.findByCertificateIdOrderByIdAsc(certId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public QuizQuestion create(@RequestBody QuizQuestion q) {
        Long certId = (q.getCertificate() != null) ? q.getCertificate().getId() : null;
        if (certId == null) throw new RuntimeException("Certificate id is required");

        var cert = certificates.findById(certId)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id " + certId));

        q.setId(null);
        q.setCertificate(cert);

        // default mark safety
        if (q.getMark() <= 0) q.setMark(1);

        return questions.save(q);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public QuizQuestion update(@PathVariable Long id, @RequestBody QuizQuestion q) {
        var existing = questions.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + id));

        // update certificate if provided
        if (q.getCertificate() != null && q.getCertificate().getId() != null) {
            var cert = certificates.findById(q.getCertificate().getId())
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));
            existing.setCertificate(cert);
        }

        existing.setText(q.getText());
        existing.setOptionA(q.getOptionA());
        existing.setOptionB(q.getOptionB());
        existing.setOptionC(q.getOptionC());
        existing.setOptionD(q.getOptionD());
        existing.setCorrectIndex(q.getCorrectIndex());

        // keep mark sane (>=1)
        existing.setMark(q.getMark() > 0 ? q.getMark() : Math.max(1, existing.getMark()));

        return questions.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        questions.deleteById(id);
    }
}
