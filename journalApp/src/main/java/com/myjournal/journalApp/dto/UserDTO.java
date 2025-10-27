package com.myjournal.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private ObjectId id;
    private String userName;
    private String password;

    public UserDTO(ObjectId id, @NonNull String userName) {
        this.id = id;
        this.userName = userName;
    }
}
