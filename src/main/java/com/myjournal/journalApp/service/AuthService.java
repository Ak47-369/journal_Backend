package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.usersDTO.CreateUserRequest;
import com.myjournal.journalApp.dto.usersDTO.LoginRequest;
import com.myjournal.journalApp.dto.usersDTO.LoginResponse;
import com.myjournal.journalApp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
    private final UserService userService;
    private final JwtTokenService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserService userService, JwtTokenService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public void register(CreateUserRequest createUseRequest){
        userService.createUser(createUseRequest);
        log.info("New User Registered Successfully: {}", createUseRequest.getUserName());
    }

    public LoginResponse login(LoginRequest loginRequest){
        // This will throw an exception if the credentials are invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );
        User user = userService.findByUserName(loginRequest.getUserName());
        String jwtToken = jwtService.generateToken(user);
        log.info("User {} logged in Successfully.", user.getUserName());
        return new LoginResponse(jwtToken);
    }
}
