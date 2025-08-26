package dali.controller;

import dali.dto.PagedResponse;
import dali.dto.QuizQuestionDTO;
import dali.dto.QuizResultResponse;
import dali.dto.QuizSubmissionRequest;
import dali.dto.SubmissionDetailDTO;
import dali.dto.SubmissionSummaryDTO;
import dali.service.QuizService;
import dali.service.QuizSubmissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
// @CrossOrigin(origins = "http://localhost:4200")  <-- REMOVE THIS
public class QuizController {

    private final QuizService quizService;
    private final QuizSubmissionService submissionService;

    public QuizController(QuizService quizService, QuizSubmissionService submissionService) {
        this.quizService = quizService;
        this.submissionService = submissionService;
    }

    @GetMapping("/{certificateId}")
    public List<QuizQuestionDTO> getQuestions(@PathVariable Long certificateId) {
        return quizService.getQuestionDTOsByCertificate(certificateId);
    }

    @PostMapping("/{certificateId}/submit")
    public QuizResultResponse submitAnswers(@PathVariable Long certificateId,
                                            @RequestBody QuizSubmissionRequest request,
                                            Authentication auth) {
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        return submissionService.submit(certificateId, auth.getName(), request.getAnswers());
    }

    @GetMapping("/submissions/mine")
    public List<SubmissionSummaryDTO> mySubmissions(Authentication auth) {
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        return submissionService.listMine(auth.getName());
    }

    @GetMapping("/submissions/{id}")
    public SubmissionDetailDTO submissionDetail(@PathVariable Long id, Authentication auth) {
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return submissionService.getDetail(id, auth.getName(), isAdmin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/submissions/certificate/{certificateId}")
    public PagedResponse<SubmissionSummaryDTO> submissionsByCertificate(@PathVariable Long certificateId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "20") int size) {
        return submissionService.listByCertificate(certificateId, page, size);
    }
}
