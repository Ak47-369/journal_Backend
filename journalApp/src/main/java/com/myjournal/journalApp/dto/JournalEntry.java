package com.myjournal.journalApp.dto;

import lombok.*;
import org.bson.types.ObjectId;

// POJO class - Plain Old Java Object
@Getter
@Setter
@NoArgsConstructor
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
