package com.example.englishapp.model;


import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String id;
    private String name;
    private String image;
    private boolean access;
    private Object create_At;
    private Object last_Update;
    private String idOwner;
    private int count;

    private String game;
    private String mode;
    private List<Vocab> vocabList = new ArrayList<>();

    public List<Vocab> getVocabList() {
        return vocabList;
    }

    public void setVocabList(List<Vocab> vocabList) {
        this.vocabList = vocabList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(String idOwner) {
        this.idOwner = idOwner;
    }

    public Topic(String id, String name, int count, String idOwner) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.idOwner = idOwner;
    }
    public Topic(String id, String name, String image, boolean access, Object create_At, Object last_Update, String idOwner) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.access = access;
        this.create_At = create_At;
        this.last_Update = last_Update;
        this.idOwner = idOwner;

    }

    public Topic(String id, String name, String image, String idOwner, int count,boolean access) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.idOwner = idOwner;
        this.count = count;
        this.access = access;
    }
    public Topic(String id, String name, String image, String idOwner, int count,boolean access,String game) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.idOwner = idOwner;
        this.count = count;
        this.access = access;
        this.game = game;
    }
    public Topic(String id, String name, String image, String idOwner,Boolean access,String game,String mode,List<Vocab> vocabList) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.idOwner = idOwner;
        this.game = game;
        this.mode = mode;
        this.vocabList = vocabList;
        this.access = access;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public Object getCreate_At() {
        return create_At;
    }

    public void setCreate_At(Object create_At) {
        this.create_At = create_At;
    }

    public Object getLast_Update() {
        return last_Update;
    }

    public void setLast_Update(Object last_Update) {
        this.last_Update = last_Update;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
