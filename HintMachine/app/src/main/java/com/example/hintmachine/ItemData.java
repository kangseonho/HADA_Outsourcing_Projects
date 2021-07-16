package com.example.hintmachine;

public class ItemData {
    private String code;
    private String hint;
    private String answer;

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCode() {
        return code;
    }

    public String getHint() {
        return hint;
    }
}
