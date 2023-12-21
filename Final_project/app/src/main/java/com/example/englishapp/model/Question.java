package com.example.englishapp.model;

import java.util.List;

public class Question {
    private String questionText;
    private List<Vocab> choices;
    private Vocab correctAnswer;

    public Question(String questionText, List<Vocab> choices, Vocab correctAnswer) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<Vocab> getChoices() {
        return choices;
    }

    public Vocab getCorrectAnswer() {
        return correctAnswer;
    }

}
