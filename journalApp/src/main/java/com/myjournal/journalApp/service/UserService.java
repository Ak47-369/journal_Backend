package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.CreateUserRequest;
import com.myjournal.journalApp.dto.UserResponse;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.enums.Roles;
import com.myjournal.journalApp.exception.ResourceNotFoundException;
import com.myjournal.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JournalEntryService journalEntryService;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    public UserService(UserRepository userRepository, JournalEntryService journalEntryService, PasswordEncoder passwordEncoder
            , MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.journalEntryService = journalEntryService;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;

    }

    public List<UserResponse> findAllUsers() {
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
            log.error("User with username '{}' already exists.", createUserRequest.getUserName());
            throw new IllegalStateException(String.format("User with username '%s' already exists.", createUserRequest.getUserName()));
        });

        User user = new User(createUserRequest.getUserName(), passwordEncoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUserName());
    }

    public UserResponse createAdminUser(CreateUserRequest createUserRequest){
        userRepository.findByUserName(createUserRequest.getUserName()).ifPresent(user -> {
            throw new IllegalStateException(String.format("User with username '%s' already exists.", createUserRequest.getUserName()));
        });

        User user = new User(createUserRequest.getUserName(), passwordEncoder.encode(createUserRequest.getPassword()));
        if (createUserRequest.getRole() != null && !createUserRequest.getRole().isEmpty()) {
            user.getRoles().add(Roles.valueOf(createUserRequest.getRole()));
        }
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

    /* Good User - Whose username starts with A(case-insenstive) and role = User only OR whose journalEntries are not empty*/
    public List<UserResponse> findGoodUsers() {
        Criteria usernameStartsWithA = Criteria.where("userName").regex("^A", "i");
        Criteria roleIsOnlyUser = new Criteria().andOperator(
                Criteria.where("roles").size(1),
                Criteria.where("roles").is(Roles.USER)
        );

        Criteria hasJournalEntries = Criteria.where("journalEntryIds").exists(true).ne(new ArrayList<>());
        Criteria userIsGoodByNameAndRole = new Criteria().andOperator(usernameStartsWithA, roleIsOnlyUser);
        // Now, create the final query: (A AND B) OR C
        Criteria mainCriteria = new Criteria().orOperator(userIsGoodByNameAndRole, hasJournalEntries);
        Query query = new Query(mainCriteria);

        log.info("Executing dynamic query for good users: {}", query); // Good Practice to log the query
        List<User> users = mongoTemplate.find(query, User.class);
        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getUserName()))
                .toList();
    }
}
