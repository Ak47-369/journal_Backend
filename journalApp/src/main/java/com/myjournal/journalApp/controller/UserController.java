package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.usersDTO.CreateUserRequest;
import com.myjournal.journalApp.dto.usersDTO.UserResponse;
import com.myjournal.journalApp.dto.weather.WeatherResponse;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.service.UserService;
import com.myjournal.journalApp.service.WeatherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername(); // Extracting userName from userDetails
        UserResponse userResponse = userService.getUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUserById( @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CreateUserRequest createUserRequest ) {
        String userName = userDetails.getUsername();
        UserResponse updatedUser = userService.updateUserById(userService.getUserIdByName(userName), createUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        userService.deleteUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/weather/{city}")
    public ResponseEntity<?> greeting(@PathVariable String city,@AuthenticationPrincipal UserDetails userDetails){
        WeatherResponse weatherResponse = weatherService.getWeather(city);
        return new ResponseEntity<>("Hi " + userDetails.getUsername() + weatherResponse.getCurrent().toString(), HttpStatus.OK);
    }
}
