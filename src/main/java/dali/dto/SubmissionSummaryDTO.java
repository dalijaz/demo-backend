package dali.dto;

import java.time.Instant;

public class SubmissionSummaryDTO {
    private Long id;
    private Long certificateId;
    private String certificateName;
    private int score;
    private int total;
    private boolean passed;
    private double percent;      // 0..100
    private Instant submittedAt;

    public SubmissionSummaryDTO() {}

    public SubmissionSummaryDTO(Long id, Long certificateId, String certificateName,
                                int score, int total, boolean passed,
                                double percent, Instant submittedAt) {
        this.id = id;
        this.certificateId = certificateId;
        this.certificateName = certificateName;
        this.score = score;
        this.total = total;
        this.passed = passed;
        this.percent = percent;
        this.submittedAt = submittedAt;
    }

    public Long getId() { return id; }
    public Long getCertificateId() { return certificateId; }
    public String getCertificateName() { return certificateName; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public boolean isPassed() { return passed; }
    public double getPercent() { return percent; }
    public Instant getSubmittedAt() { return submittedAt; }

    public void setId(Long id) { this.id = id; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public void setCertificateName(String certificateName) { this.certificateName = certificateName; }
    public void setScore(int score) { this.score = score; }
    public void setTotal(int total) { this.total = total; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setPercent(double percent) { this.percent = percent; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}
