package dali.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "quiz_submissions",
    indexes = {
        @Index(name = "idx_qs_user", columnList = "user_id"),
        @Index(name = "idx_qs_certificate", columnList = "certificate_id"),
        @Index(name = "idx_qs_submitted_at", columnList = "submitted_at")
    }
)
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int total;     // total possible marks

    @Column(nullable = false)
    private boolean passed;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    // NEW: back-reference to answers (so getAnswers() is available)
    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY)
    private List<QuizSubmissionAnswer> answers = new ArrayList<>();

    public QuizSubmission() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Certificate getCertificate() { return certificate; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public boolean isPassed() { return passed; }
    public Instant getSubmittedAt() { return submittedAt; }
    public List<QuizSubmissionAnswer> getAnswers() { return answers; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setCertificate(Certificate certificate) { this.certificate = certificate; }
    public void setScore(int score) { this.score = score; }
    public void setTotal(int total) { this.total = total; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public void setAnswers(List<QuizSubmissionAnswer> answers) { this.answers = answers; }
}
