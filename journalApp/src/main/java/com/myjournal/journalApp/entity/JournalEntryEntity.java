package com.myjournal.journalApp.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "journal_entries")
@Data
public class JournalEntryEntity {
    @Id
    private ObjectId id;
    private String title;
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

//    // Similar to Jackson, Entity also need default constructor for creating object from database data
    public JournalEntryEntity(String content, String title) {
        this.title = title;
        this.content = content;
    }
}
