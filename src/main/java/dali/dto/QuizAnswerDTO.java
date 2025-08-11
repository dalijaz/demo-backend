package dali.dto;

public class QuizAnswerDTO {
    private Long questionId;   // quiz_questions.id
    private int chosenIndex;   // 0..3

    public QuizAnswerDTO() { }
    public QuizAnswerDTO(Long questionId, int chosenIndex) {
        this.questionId = questionId;
        this.chosenIndex = chosenIndex;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public int getChosenIndex() { return chosenIndex; }
    public void setChosenIndex(int chosenIndex) { this.chosenIndex = chosenIndex; }

    // Back-compat for any old code that called getUserAnswer()
    public int getUserAnswer() { return chosenIndex; }
}
