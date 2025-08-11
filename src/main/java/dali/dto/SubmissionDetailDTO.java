package dali.dto;

import java.time.Instant;
import java.util.List;

public class SubmissionDetailDTO {
    private Long id;
    private Long certificateId;
    private String certificateName;
    private int score;
    private int total;
    private boolean passed;
    private double percent;
    private Instant submittedAt;
    private List<AnswerDetail> answers;

    public static class AnswerDetail {
        private Long questionId;
        private String text;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private int chosenIndex;   // 0..3
        private int correctIndex;  // 0..3
        private boolean correct;

        public AnswerDetail() {}

        public AnswerDetail(Long questionId, String text,
                            String optionA, String optionB, String optionC, String optionD,
                            int chosenIndex, int correctIndex, boolean correct) {
            this.questionId = questionId;
            this.text = text;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.chosenIndex = chosenIndex;
            this.correctIndex = correctIndex;
            this.correct = correct;
        }

        public Long getQuestionId() { return questionId; }
        public String getText() { return text; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public int getChosenIndex() { return chosenIndex; }
        public int getCorrectIndex() { return correctIndex; }
        public boolean isCorrect() { return correct; }

        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public void setText(String text) { this.text = text; }
        public void setOptionA(String optionA) { this.optionA = optionA; }
        public void setOptionB(String optionB) { this.optionB = optionB; }
        public void setOptionC(String optionC) { this.optionC = optionC; }
        public void setOptionD(String optionD) { this.optionD = optionD; }
        public void setChosenIndex(int chosenIndex) { this.chosenIndex = chosenIndex; }
        public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
        public void setCorrect(boolean correct) { this.correct = correct; }
    }

    public SubmissionDetailDTO() {}

    public SubmissionDetailDTO(Long id, Long certificateId, String certificateName,
                               int score, int total, boolean passed, double percent,
                               Instant submittedAt, List<AnswerDetail> answers) {
        this.id = id;
        this.certificateId = certificateId;
        this.certificateName = certificateName;
        this.score = score;
        this.total = total;
        this.passed = passed;
        this.percent = percent;
        this.submittedAt = submittedAt;
        this.answers = answers;
    }

    public Long getId() { return id; }
    public Long getCertificateId() { return certificateId; }
    public String getCertificateName() { return certificateName; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public boolean isPassed() { return passed; }
    public double getPercent() { return percent; }
    public Instant getSubmittedAt() { return submittedAt; }
    public List<AnswerDetail> getAnswers() { return answers; }

    public void setId(Long id) { this.id = id; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }
    public void setCertificateName(String certificateName) { this.certificateName = certificateName; }
    public void setScore(int score) { this.score = score; }
    public void setTotal(int total) { this.total = total; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setPercent(double percent) { this.percent = percent; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
    public void setAnswers(List<AnswerDetail> answers) { this.answers = answers; }
}
