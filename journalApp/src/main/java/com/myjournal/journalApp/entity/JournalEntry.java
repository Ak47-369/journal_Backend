package com.myjournal.journalApp.entity;

// POJO class - Plain Old Java Object
public class JournalEntry {
    private long id;
    private String title;
    private String content;

    // Deafult constructor is needed for Jackson (to convert JSON to Journal Entry)
    public JournalEntry(){

    }

    public JournalEntry(long id, String title, String content){
        this.content = content;
        this.id  = id;
        this.title = title;
    }

    public JournalEntry(String content, String title){
        this.content = content;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
