package dali.dto;

public class QuizQuestionDTO {
    private Long id;
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int mark;

    public QuizQuestionDTO() {}

    public QuizQuestionDTO(Long id, String text,
                           String optionA, String optionB, String optionC, String optionD,
                           int mark) {
        this.id = id;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.mark = mark;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public int getMark() { return mark; }

    public void setId(Long id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setMark(int mark) { this.mark = mark; }
}
