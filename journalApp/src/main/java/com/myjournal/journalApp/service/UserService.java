package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.usersDTO.CreateUserRequest;
import com.myjournal.journalApp.dto.usersDTO.UserResponse;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.enums.Roles;
import com.myjournal.journalApp.exception.ResourceNotFoundException;
import com.myjournal.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final EmailService emailService;

    public UserService(UserRepository userRepository, JournalEntryService journalEntryService, PasswordEncoder passwordEncoder
            , MongoTemplate mongoTemplate, EmailService emailService) {
        this.userRepository = userRepository;
        this.journalEntryService = journalEntryService;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;

    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUserName()))
                .toList();
    }

    public boolean hasJournalEntryWithId(User user, ObjectId id) {
        return user.getJournalEntryIds().contains(id);
    }

    public void deleteJournalEntryById(ObjectId id, User user) {
        journalEntryService.deleteJournalEntryById(id,user);
    }

    /**
     * Caches the result of this method. The cache name is "userCache" and the key is the user's id.
     * If this method is called again with the same id, the result will be returned from the cache
     * without executing the method body.
     */
    @Cacheable(value = "userCache", key = "#id")
    public UserResponse getUserById(ObjectId id) {
        log.info("Cache Mis!!,Fetching user from DB for id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserResponse(user.getId(), user.getUserName());
    }

    /**
     * Evicts (removes) an entry from the "userCache".
     * The key to be evicted is determined by the 'id' parameter of this method.
     * This ensures that when a user is updated, the stale data is removed from the cache.
     */
    @CacheEvict(value = "userCache", key = "#id")
    public UserResponse updateUserById(ObjectId id, CreateUserRequest createUserRequest) {
        log.info("Evicting user from cache for id: {}", id);
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

    /**
     * Evicts an entry from the "userCache" when a user is deleted.
     */
    @CacheEvict(value = "userCache", key = "#id")
    public void deleteUserById(ObjectId id) {
        log.info("Evicting user from cache for id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        // To DO - Delete journal entries also
        userRepository.deleteById(id);
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        // Check if username already exists
        userRepository.findByUserName(createUserRequest.getUserName()).ifPresent(user -> {
            log.error("User with username '{}' already exists.", createUserRequest.getUserName());
            throw new IllegalStateException(String.format("User with username '%s' already exists.", createUserRequest.getUserName()));
        });

        User user = new User(createUserRequest.getUserName(), passwordEncoder.encode(createUserRequest.getPassword()),createUserRequest.getEmail());
        User savedUser = userRepository.save(user);
        emailService.sendEmail(
                user.getEmail(),
                "Welcome to Journal App!",
                "Hello " + createUserRequest.getUserName() + ",\n\nWelcome!!🎉🎉",

                "user:" + user.getId());
        return new UserResponse(savedUser.getId(), savedUser.getUserName());
    }

    public UserResponse createAdminUser(CreateUserRequest createUserRequest){
        userRepository.findByUserName(createUserRequest.getUserName()).ifPresent(user -> {
            throw new IllegalStateException(String.format("User with username '%s' already exists.", createUserRequest.getUserName()));
        });

        User user = new User(createUserRequest.getUserName(), passwordEncoder.encode(createUserRequest.getPassword()),createUserRequest.getEmail());
        if (createUserRequest.getRole() != null && !createUserRequest.getRole().isEmpty()) {
            user.getRoles().add(Roles.valueOf(createUserRequest.getRole()));
        }
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUserName());
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
