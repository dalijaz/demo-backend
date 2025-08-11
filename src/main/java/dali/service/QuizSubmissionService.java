package dali.service;

import dali.dto.PagedResponse;
import dali.dto.QuizAnswerDTO;
import dali.dto.QuizResultResponse;
import dali.dto.SubmissionDetailDTO;
import dali.dto.SubmissionSummaryDTO;
import dali.model.Certificate;
import dali.model.QuizQuestion;
import dali.model.QuizSubmission;
import dali.model.QuizSubmissionAnswer;
import dali.model.User;
import dali.repository.CertificateRepository;
import dali.repository.QuizQuestionRepository;
import dali.repository.QuizSubmissionAnswerRepository;
import dali.repository.QuizSubmissionRepository;
import dali.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuizSubmissionService {

    private final QuizQuestionRepository questionRepo;
    private final CertificateRepository certificateRepo;
    private final UserRepository userRepo;
    private final QuizSubmissionRepository submissionRepo;
    private final QuizSubmissionAnswerRepository answerRepo;

    public QuizSubmissionService(QuizQuestionRepository questionRepo,
                                 CertificateRepository certificateRepo,
                                 UserRepository userRepo,
                                 QuizSubmissionRepository submissionRepo,
                                 QuizSubmissionAnswerRepository answerRepo) {
        this.questionRepo = questionRepo;
        this.certificateRepo = certificateRepo;
        this.userRepo = userRepo;
        this.submissionRepo = submissionRepo;
        this.answerRepo = answerRepo;
    }

    // ------------------------ WRITE: submit ------------------------
    @Transactional
    public QuizResultResponse submit(Long certificateId, String userEmail, List<QuizAnswerDTO> answers) {
        Certificate cert = certificateRepo.findById(certificateId)
            .orElseThrow(() -> new IllegalArgumentException("Certificate not found: " + certificateId));

        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        List<QuizQuestion> questions = questionRepo.findByCertificateIdOrderByIdAsc(certificateId);
        if (questions.isEmpty()) {
            QuizSubmission empty = new QuizSubmission();
            empty.setUser(user);
            empty.setCertificate(cert);
            empty.setScore(0);
            empty.setTotal(0);
            empty.setPassed(false);
            empty = submissionRepo.save(empty);
            return new QuizResultResponse(0, 0, 0, false, empty.getId());
        }

        Map<Long, QuizQuestion> byId = questions.stream()
            .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));

        int totalMarks = questions.stream().mapToInt(q -> Math.max(1, q.getMark())).sum();

        int score = 0;
        if (answers != null && !answers.isEmpty()) {
            for (QuizAnswerDTO a : answers) {
                QuizQuestion q = byId.get(a.getQuestionId());
                if (q == null) continue;
                int idx = a.getChosenIndex();
                if (idx < 0 || idx > 3) continue;
                if (q.getCorrectIndex() == idx) {
                    score += Math.max(1, q.getMark());
                }
            }
        }

        boolean passed = (totalMarks == 0) ? false : (score * 100) >= (60 * totalMarks);

        QuizSubmission sub = new QuizSubmission();
        sub.setUser(user);
        sub.setCertificate(cert);
        sub.setScore(score);
        sub.setTotal(totalMarks);
        sub.setPassed(passed);
        sub = submissionRepo.save(sub);

        final QuizSubmission savedSub = sub;

        if (answers != null && !answers.isEmpty()) {
            List<QuizSubmissionAnswer> toSave = answers.stream()
                .map(a -> {
                    QuizQuestion q = byId.get(a.getQuestionId());
                    if (q == null) return null;
                    int idx = a.getChosenIndex();
                    if (idx < 0 || idx > 3) return null;

                    QuizSubmissionAnswer ans = new QuizSubmissionAnswer();
                    ans.setSubmission(savedSub);
                    ans.setQuestion(q);
                    ans.setChosenIndex(idx);
                    ans.setCorrect(q.getCorrectIndex() == idx);
                    return ans;
                })
                .filter(x -> x != null)
                .toList();

            if (!toSave.isEmpty()) {
                answerRepo.saveAll(toSave);
            }
        }

        return new QuizResultResponse(questions.size(), totalMarks, score, passed, sub.getId());
    }

    // ------------------------ READ: history/detail ------------------------

    @Transactional(readOnly = true)
    public List<SubmissionSummaryDTO> listMine(String userEmail) {
        var rows = submissionRepo.findByUserEmailIgnoreCaseOrderBySubmittedAtDesc(userEmail);
        List<SubmissionSummaryDTO> out = new ArrayList<>(rows.size());
        for (var s : rows) {
            double pct = (s.getTotal() == 0) ? 0.0 : (s.getScore() * 100.0 / s.getTotal());
            out.add(new SubmissionSummaryDTO(
                s.getId(),
                s.getCertificate().getId(),
                s.getCertificate().getName(),
                s.getScore(),
                s.getTotal(),
                s.isPassed(),
                pct,
                s.getSubmittedAt()
            ));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public SubmissionDetailDTO getDetail(Long submissionId, String requesterEmail, boolean isAdmin) {
        var opt = submissionRepo.findWithAnswersById(submissionId);
        var s = opt.orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        if (!isAdmin && !s.getUser().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new AccessDeniedException("Not allowed to view this submission");
        }

        double pct = (s.getTotal() == 0) ? 0.0 : (s.getScore() * 100.0 / s.getTotal());

        var answerDetails = s.getAnswers()
            .stream()
            .sorted(Comparator.comparing(a -> a.getQuestion().getId()))
            .map(a -> new SubmissionDetailDTO.AnswerDetail(
                a.getQuestion().getId(),
                a.getQuestion().getText(),
                a.getQuestion().getOptionA(),
                a.getQuestion().getOptionB(),
                a.getQuestion().getOptionC(),
                a.getQuestion().getOptionD(),
                a.getChosenIndex(),
                a.getQuestion().getCorrectIndex(),
                a.isCorrect()
            ))
            .toList();

        // Build DTO via setters (avoids constructor signature mismatch)
        SubmissionDetailDTO dto = new SubmissionDetailDTO();
        dto.setId(s.getId());
        dto.setCertificateId(s.getCertificate().getId());
        dto.setCertificateName(s.getCertificate().getName());
        dto.setScore(s.getScore());
        dto.setTotal(s.getTotal());
        dto.setPassed(s.isPassed());
        dto.setPercent(pct);
        dto.setSubmittedAt(s.getSubmittedAt());
        dto.setAnswers(answerDetails);

        return dto;
    }

    @Transactional(readOnly = true)
    public PagedResponse<SubmissionSummaryDTO> listByCertificate(Long certificateId, int page, int size) {
        Page<QuizSubmission> p = submissionRepo.findByCertificateIdOrderBySubmittedAtDesc(
            certificateId, PageRequest.of(page, size)
        );

        List<SubmissionSummaryDTO> content = p.getContent().stream()
            .map(s -> new SubmissionSummaryDTO(
                s.getId(),
                s.getCertificate().getId(),
                s.getCertificate().getName(),
                s.getScore(),
                s.getTotal(),
                s.isPassed(),
                (s.getTotal() == 0) ? 0.0 : (s.getScore() * 100.0 / s.getTotal()),
                s.getSubmittedAt()
            ))
            .toList();

        return new PagedResponse<>(content, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
}
