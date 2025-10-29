package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.CreateUserRequest;
import com.myjournal.journalApp.dto.UserResponse;
import com.myjournal.journalApp.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserResponse createdUser = userService.createUser(createUserRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName(); // Extracting userName from Authentication
        UserResponse userResponse = userService.getUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUserById( @Valid @RequestBody CreateUserRequest createUserRequest ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName(); // Extracting userName from Authentication
        UserResponse updatedUser = userService.updateUserById(userService.getUserIdByName(userName), createUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName(); // Extracting userName from Authentication
        userService.deleteUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // To DO - Only ADMIN should be able to access this endpoint
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> allUsers = userService.findAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }
}
