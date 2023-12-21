package com.example.englishapp.model;

import android.os.Parcelable;

public class Vocab  {
    private String id="";
    private String word ="";
    private String trans ="";
    private String image ="";
    private String process ="";
    private String fav ="";
    private String idTopic = "";
    public Vocab(String id, String word, String trans, String image, String process, String fav) {
        this.id = id;
        this.word = word;
        this.trans = trans;
        this.image = image;
        this.process = process;
        this.fav = fav;
    }

    public Vocab(String id, String word, String trans, String image, String process, String fav, String idTopic) {
        this.id = id;
        this.word = word;
        this.trans = trans;
        this.image = image;
        this.process = process;
        this.fav = fav;
        this.idTopic = idTopic;
    }

    public Vocab(String word, String trans)
    {
        this.word = word;
        this.trans = trans;
    }

    public String getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(String idTopic) {
        this.idTopic = idTopic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

}
