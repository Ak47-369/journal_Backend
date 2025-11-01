package com.myjournal.journalApp.entity;

import com.myjournal.journalApp.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @NotBlank(message = "Username can't be blank")
    private String userName;
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Invalid email format")
    private String email;
    private List<ObjectId> journalEntryIds = new ArrayList<>();
    @NotBlank(message = "Password can't be blank")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;
    private List<Roles> roles = new ArrayList<>(); // Initialize to avoid NullPointerException

    public User(@NonNull String userName, @NonNull String password, @NonNull String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.roles.add(Roles.USER); // Default role for new users
    }
}
