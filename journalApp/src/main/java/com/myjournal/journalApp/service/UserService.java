package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.UserDTO;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.exception.ResourceNotFoundException;
import com.myjournal.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JournalEntryService journalEntryService;

    public UserService(UserRepository userRepository, JournalEntryService journalEntryService) {
        this.userRepository = userRepository;
        this.journalEntryService = journalEntryService;
    }

    public List<UserDTO> getAllEntries() {
        return userRepository.findAll().stream().map(user -> new UserDTO(user.getId(), user.getUserName())).toList();
    }

    @Transactional
    public void deleteJournalEntryById(User user, ObjectId id) {
        user.getJournalEntryIds().remove(id);
        userRepository.save(user);
        journalEntryService.deleteJournalEntryById(id);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User(userDTO.getUserName(), userDTO.getPassword());
        User savedEntry = userRepository.save(user);
        return new UserDTO(savedEntry.getId(), savedEntry.getUserName());
    }

    public UserDTO getUserById(ObjectId id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserDTO(user.getId(), user.getUserName());
    }

    public UserDTO updateUserById(ObjectId id, UserDTO newUserDTO) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (newUserDTO.getUserName() != null && !newUserDTO.getUserName().isEmpty()) {
            oldUser.setUserName(newUserDTO.getUserName());
        }
        User savedEntry = userRepository.save(oldUser);
        return new UserDTO(savedEntry.getId(), savedEntry.getUserName());
    }

    public void deleteUserById(ObjectId id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(user -> new UserDTO(user.getId(), user.getUserName())).toList();
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() -> new ResourceNotFoundException("User", "userName", userName));
    }

}
