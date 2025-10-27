package com.myjournal.journalApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

// POJO class - Plain Old Java Object
@Data
@AllArgsConstructor
public class JournalEntry {
    private ObjectId id;
    private String title;
    private String content;

    // Deafult constructor is needed for Jackson (to convert JSON to Journal Entry)
    public JournalEntry(String content, String title){
        this.content = content;
        this.title = title;
    }
}
