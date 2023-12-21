package com.example.englishapp.model;

import com.google.type.DateTime;

import java.util.Date;
import java.util.List;

public class Folder {
    private String id;
    private String name;
    private List<String> id_topic;
    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Folder(String id, String name, List<String> id_topic, Date time) {
        this.id = id;
        this.name = name;
        this.id_topic = id_topic;
        this.time = time;
    }

    public String getId() {
        return id;
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

    public List<String> getId_topic() {
        return id_topic;
    }

    public void setId_topic(List<String> id_topic) {
        this.id_topic = id_topic;
    }
}
