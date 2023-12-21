package com.example.englishapp.model;

public class Quizz {
    private String id;
    private String idTopic;
    private long score;
    private long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(String idTopic) {
        this.idTopic = idTopic;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Quizz(long score, long time) {
        this.score = score;
        this.time = time;
    }

    public Quizz(String id, String idTopic,long score, long time) {
        this.id = id;
        this.idTopic = idTopic;
        this.score = score;
        this.time = time;
    }
}
