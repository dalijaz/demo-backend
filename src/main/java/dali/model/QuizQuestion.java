package dali.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    // ✅ Map to the DB column 'question_text'
    @Column(name = "question_text", nullable = false, length = 500)
    private String text;

    @Column(name = "optiona", nullable = false)
    private String optionA;

    @Column(name = "optionb", nullable = false)
    private String optionB;

    @Column(name = "optionc", nullable = false)
    private String optionC;

    @Column(name = "optiond", nullable = false)
    private String optionD;

    @Column(name = "correct_index", nullable = false)
    private int correctIndex; // 0..3

    @Column(nullable = false)
    private int mark = 1;

    public QuizQuestion() {}

    public QuizQuestion(String text,
                        String optionA,
                        String optionB,
                        String optionC,
                        String optionD,
                        int correctIndex,
                        Certificate certificate) {
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctIndex = correctIndex;
        this.certificate = certificate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Certificate getCertificate() { return certificate; }
    public void setCertificate(Certificate certificate) { this.certificate = certificate; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }

    public int getMark() { return mark; }
    public void setMark(int mark) { this.mark = mark; }

    // Convenience helpers
    public String getQuestionText() { return text; }
    public String getCorrectAnswer() {
        return switch (correctIndex) {
            case 0 -> optionA;
            case 1 -> optionB;
            case 2 -> optionC;
            case 3 -> optionD;
            default -> null;
        };
    }
}
