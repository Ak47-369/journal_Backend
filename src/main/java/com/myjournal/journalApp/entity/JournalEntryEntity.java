package com.myjournal.journalApp.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
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
    @NotBlank
    private String title;
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

 // Similar to Jackson, Entity also need default constructor for creating object from database data
    public JournalEntryEntity(String content, @NotBlank String title) {
        this.title = title;
        this.content = content;
    }
}
