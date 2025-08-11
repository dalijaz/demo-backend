package dali.service;

import dali.dto.QuizAnswerDTO;
import dali.dto.QuizQuestionDTO;
import dali.dto.QuizResultResponse;
import dali.model.QuizQuestion;
import dali.repository.CertificateRepository;
import dali.repository.QuizQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizQuestionRepository questionRepo;
    private final CertificateRepository certificateRepo;

    public QuizService(QuizQuestionRepository questionRepo, CertificateRepository certificateRepo) {
        this.questionRepo = questionRepo;
        this.certificateRepo = certificateRepo;
    }

    public List<QuizQuestionDTO> getQuestionDTOsByCertificate(Long certificateId) {
        List<QuizQuestion> qs = questionRepo.findByCertificateIdOrderByIdAsc(certificateId);
        return qs.stream()
                .map(q -> new QuizQuestionDTO(
                        q.getId(),
                        q.getText(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD(),
                        Math.max(1, q.getMark())
                ))
                .collect(Collectors.toList());
    }

    // Non-persisting grader (kept for any old call sites)
    public QuizResultResponse gradeSubmission(Long certificateId, List<QuizAnswerDTO> answers) {
        List<QuizQuestion> qs = questionRepo.findByCertificateIdOrderByIdAsc(certificateId);
        int totalMarks = qs.stream().mapToInt(q -> Math.max(1, q.getMark())).sum();

        var byId = qs.stream().collect(Collectors.toMap(QuizQuestion::getId, q -> q));
        int score = 0;
        for (QuizAnswerDTO a : answers) {
            var q = byId.get(a.getQuestionId());
            if (q == null) continue;
            if (q.getCorrectIndex() == a.getChosenIndex()) {
                score += Math.max(1, q.getMark());
            }
        }
        boolean passed = score * 100 >= 60 * totalMarks;
        return new QuizResultResponse(qs.size(), totalMarks, score, passed);
    }
}
