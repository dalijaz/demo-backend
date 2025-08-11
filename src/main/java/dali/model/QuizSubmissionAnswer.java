package dali.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "quiz_submission_answers",
    indexes = {
        @Index(name = "idx_qsa_submission", columnList = "submission_id"),
        @Index(name = "idx_qsa_question", columnList = "question_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_submission_question",
            columnNames = {"submission_id", "question_id"}
        )
    }
)
public class QuizSubmissionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private QuizSubmission submission;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "chosen_index", nullable = false)
    private int chosenIndex; // 0..3

    @Column(nullable = false)
    private boolean correct;

    public QuizSubmissionAnswer() {}

    public Long getId() { return id; }
    public QuizSubmission getSubmission() { return submission; }
    public QuizQuestion getQuestion() { return question; }
    public int getChosenIndex() { return chosenIndex; }
    public boolean isCorrect() { return correct; }

    public void setId(Long id) { this.id = id; }
    public void setSubmission(QuizSubmission submission) { this.submission = submission; }
    public void setQuestion(QuizQuestion question) { this.question = question; }
    public void setChosenIndex(int chosenIndex) { this.chosenIndex = chosenIndex; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
