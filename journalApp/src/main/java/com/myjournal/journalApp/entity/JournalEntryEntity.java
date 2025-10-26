package com.myjournal.journalApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.processing.Generated;

@Document
public class JournalEntryEntity {
    @Id
    private Long id;
    private String title;
    private String content;

    public JournalEntryEntity(Long id, String content, String title) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
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
