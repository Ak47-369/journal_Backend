package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.CreateUserRequest;
import com.myjournal.journalApp.dto.UserResponse;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> allUsers = userService.findAllUsers();
        if(allUsers != null && !allUsers.isEmpty()){
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<UserResponse> crateAdminUser(@RequestBody CreateUserRequest createUserRequest){
        createUserRequest.setRole("ADMIN");
        UserResponse createdUser = userService.createAdminUser(createUserRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/good-users")
    public ResponseEntity<List<UserResponse>> findGoodUsers(){
        List<UserResponse> goodUsers = userService.findGoodUsers();
        if(goodUsers != null && !goodUsers.isEmpty()){
            return new ResponseEntity<>(goodUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
