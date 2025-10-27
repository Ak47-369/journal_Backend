package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.UserDTO;
import com.myjournal.journalApp.service.UserService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO){
        UserDTO createdUser = userService.createUser(userDTO);
        if(createdUser != null)
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable ObjectId id){
       UserDTO userDTO = userService.getUserById(id);
       if(userDTO != null)
           return new ResponseEntity<>(userDTO, HttpStatus.OK);
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/id/{id}")
    public  ResponseEntity<UserDTO> updateUserById(@PathVariable ObjectId id, @RequestBody UserDTO userDTO){
        UserDTO updatedUser = userService.updateUserById(id, userDTO);
        if(updatedUser != null)
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{userName}")
    public  ResponseEntity<UserDTO> updateUser(@PathVariable String userName ,@RequestBody UserDTO userDTO){
        UserDTO updatedUser = userService.updateUser(userName,userDTO);
        if(updatedUser != null)
            return new ResponseEntity<>(updatedUser,HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable ObjectId id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.findAllUsers();
        if(allUsers != null && !allUsers.isEmpty())
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
