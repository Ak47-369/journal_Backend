package com.myjournal.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private ObjectId id;
    private String userName;
}
