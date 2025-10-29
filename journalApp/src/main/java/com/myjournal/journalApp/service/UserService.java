package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.CreateUserRequest;
import com.myjournal.journalApp.dto.UserResponse;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.exception.ResourceNotFoundException;
import com.myjournal.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JournalEntryService journalEntryService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JournalEntryService journalEntryService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.journalEntryService = journalEntryService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAllUsers() { // Renamed from getAllUsers for clarity
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUserName()))
                .toList();
    }

    public boolean hasJournalEntryWithId(User user, ObjectId id) {
        return user.getJournalEntryIds().contains(id);
    }

    public void deleteJournalEntryById(User user, ObjectId id) {
        user.getJournalEntryIds().remove(id);
        userRepository.save(user);
        journalEntryService.deleteJournalEntryById(id, user);
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        // Check if username already exists
        userRepository.findByUserName(createUserRequest.getUserName()).ifPresent(user -> {
            throw new IllegalStateException(String.format("User with username '%s' already exists.", createUserRequest.getUserName()));
        });

        User user = new User(createUserRequest.getUserName(), passwordEncoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUserName());
    }

    public UserResponse getUserById(ObjectId id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserResponse(user.getId(), user.getUserName());
    }

    public UserResponse updateUserById(ObjectId id, CreateUserRequest createUserRequest) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (createUserRequest.getUserName() != null && !createUserRequest.getUserName().isEmpty()) {
            oldUser.setUserName(createUserRequest.getUserName());
        }
        if (createUserRequest.getPassword() != null && !createUserRequest.getPassword().isEmpty()){
            oldUser.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        }
        User savedUser = userRepository.save(oldUser);
        return new UserResponse(savedUser.getId(), savedUser.getUserName());
    }

    public void deleteUserById(ObjectId id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        // To DO - Delete journal entries also
        userRepository.deleteById(id);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() -> new ResourceNotFoundException("User", "userName", userName));
    }

    public ObjectId getUserIdByName(String userName){
        return userRepository.findByUserName(userName).orElseThrow( () -> new ResourceNotFoundException("User", "userName", userName)).getId();
    }
}
