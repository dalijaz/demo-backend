package dali.dto;

public class QuizResultResponse {
    private int totalQuestions;
    private int totalMarks;
    private int score;
    private boolean passed;

    // id of persisted submission (nullable if not stored)
    private Long submissionId;

    public QuizResultResponse() { }

    // Old ctor kept for back-compat
    public QuizResultResponse(int totalQuestions, int totalMarks, int score, boolean passed) {
        this(totalQuestions, totalMarks, score, passed, null);
    }

    public QuizResultResponse(int totalQuestions, int totalMarks, int score, boolean passed, Long submissionId) {
        this.totalQuestions = totalQuestions;
        this.totalMarks = totalMarks;
        this.score = score;
        this.passed = passed;
        this.submissionId = submissionId;
    }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }
}
