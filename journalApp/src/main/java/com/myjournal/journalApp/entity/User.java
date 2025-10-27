package com.myjournal.journalApp.entity;

import com.myjournal.journalApp.dto.JournalEntry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    private ObjectId id;
    @Indexed(unique = true) // Unique Username
    @NonNull // Username can't be null, if it's null NullPointerException is Thrown
    private String userName;
    private List<ObjectId> journalEntryIds = new ArrayList<>();
    @NonNull
    private String password;

    public User(@NonNull String userName, @NonNull String password) {
        this.userName = userName;
        this.password = password;
    }
}
