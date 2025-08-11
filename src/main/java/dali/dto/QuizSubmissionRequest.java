package dali.dto;

import java.util.List;

public class QuizSubmissionRequest {
    private Long certificateId;               // optional; path param is authoritative
    private List<QuizAnswerDTO> answers;      // one entry per question

    public QuizSubmissionRequest() { }
    public QuizSubmissionRequest(Long certificateId, List<QuizAnswerDTO> answers) {
        this.certificateId = certificateId;
        this.answers = answers;
    }

    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }

    public List<QuizAnswerDTO> getAnswers() { return answers; }
    public void setAnswers(List<QuizAnswerDTO> answers) { this.answers = answers; }
}
