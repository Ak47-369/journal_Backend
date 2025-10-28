package com.myjournal.journalApp.entity;

import com.myjournal.journalApp.enums.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    // Use Set<Role> for roles to ensure uniqueness and type safety
    private Set<Roles> roles = new java.util.HashSet<>(); // Initialize to avoid NullPointerException

    public User(@NonNull String userName, @NonNull String password) {
        this.userName = userName;
        this.password = password;
        this.roles.add(Roles.USER); // Default role for new users
    }
}
