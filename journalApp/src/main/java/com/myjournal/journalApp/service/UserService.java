package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.JournalEntry;
import com.myjournal.journalApp.dto.UserDTO;
import com.myjournal.journalApp.entity.JournalEntryEntity;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.repository.JournalEntryRepository;
import com.myjournal.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JournalEntryService journalEntryService;

    public UserService(UserRepository userRepository, JournalEntryService journalEntryService){
        this.userRepository = userRepository;
        this.journalEntryService = journalEntryService;
    }

    public List<UserDTO> getAllEntries(){
        List<UserDTO> allEntries;
        allEntries = userRepository.findAll().stream().map(user -> new UserDTO(user.getId(),user.getUserName())).toList();
        return allEntries;
    }

    public boolean hasJournalEntryWithId(User user,ObjectId id){
        return user.getJournalEntryIds().contains(id);
    }

    public void deleteJournalEntryById(User user,ObjectId id){
        user.getJournalEntryIds().remove(id);
        userRepository.save(user);
        journalEntryService.deleteJournalEntryById(id);
    }

    public UserDTO createUser(UserDTO userDTO){
        User user = new User(userDTO.getUserName(), userDTO.getPassword());
        User savedEntry = userRepository.save(user);
        return new UserDTO(savedEntry.getId(), savedEntry.getUserName());
    }

    public UserDTO getUserById(ObjectId id){
        Optional<User> optionalUserId = userRepository.findById(id);
        if(optionalUserId.isPresent()){
            User user = optionalUserId.get();
            return new UserDTO(user.getId(), user.getUserName());
        }
        return null;
    }

    public UserDTO updateUserById(ObjectId id, UserDTO newUserDTO){
        User oldUser = userRepository.findById(id).orElse(null);
        if(oldUser != null){
            if(!oldUser.getUserName().isEmpty())
                oldUser.setUserName(newUserDTO.getUserName());
            User savedEntry = userRepository.save(oldUser);
            return new UserDTO(savedEntry.getId(),savedEntry.getUserName());
        }
        return null;
    }

    public UserDTO updateUser(String userName, UserDTO userDTO){
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        if(optionalUser.isPresent()){
            User oldUser = optionalUser.get();
            oldUser.setUserName(userDTO.getUserName());
            oldUser.setPassword(userDTO.getPassword());
            User savedUser = userRepository.save(oldUser);
            return new UserDTO(savedUser.getId(), savedUser.getUserName());
        }
        return null;
    }

    public void deleteUserById(ObjectId id){
        userRepository.deleteById(id);
    }

    public List<UserDTO> findAllUsers(){
        return userRepository.findAll().stream().map(user -> new UserDTO(user.getId(),user.getUserName())).toList();
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName).orElse(null);
    }

}
