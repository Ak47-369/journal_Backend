package com.myjournal.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private ObjectId id;
    private String userName;
}
