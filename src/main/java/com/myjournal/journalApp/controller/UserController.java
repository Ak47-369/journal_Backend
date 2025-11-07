package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.usersDTO.CreateUserRequest;
import com.myjournal.journalApp.dto.usersDTO.UserResponse;
import com.myjournal.journalApp.dto.weather.WeatherResponse;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.service.UserService;
import com.myjournal.journalApp.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Controller", description  = " CRUD Operations for User")
public class UserController {
    private final UserService userService;
    private final WeatherService weatherService;

    @Operation(summary = "Get a User by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername(); // Extracting userName from userDetails
        UserResponse userResponse = userService.getUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @Operation(summary = "Update a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping
    public ResponseEntity<UserResponse> updateUserById( @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CreateUserRequest createUserRequest ) {
        String userName = userDetails.getUsername();
        UserResponse updatedUser = userService.updateUserById(userService.getUserIdByName(userName), createUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping
    public ResponseEntity<?> deleteUserById(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        userService.deleteUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get weather by city")
     @ApiResponses(value = {
             @ApiResponse(responseCode = "200", description = "Weather retrieved successfully"),
             @ApiResponse(responseCode = "400", description = "Bad Request"),
             @ApiResponse(responseCode = "404", description = "User not found"),
             @ApiResponse(responseCode = "500", description = "Internal Server Error")
     })
    @GetMapping("/weather/{city}")
    public ResponseEntity<?> greeting(@PathVariable String city,@AuthenticationPrincipal UserDetails userDetails){
        WeatherResponse weatherResponse = weatherService.getWeather(city);
        return new ResponseEntity<>("Hi " + userDetails.getUsername() + weatherResponse.getCurrent().toString(), HttpStatus.OK);
    }
}
