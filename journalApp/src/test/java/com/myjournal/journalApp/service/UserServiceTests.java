//package com.myjournal.journalApp.service;
//
//import com.myjournal.journalApp.entity.User;
//import com.myjournal.journalApp.exception.ResourceNotFoundException;
//import com.myjournal.journalApp.repository.UserRepository;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class) // Initialize mocks and inject dependencies
//class UserServiceTests {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    private static User testUser;
//
//    @BeforeAll
//    static void setUp(){
//        testUser = new User("ram", "hashed_password_from_db","email@gmail.com");
//    }
//
//    @Test
//    @DisplayName("should return user, if username exists")
//    void findByUserName_returns_user(){
//       when(userRepository.findByUserName(anyString()))
//               .thenReturn(Optional.of(testUser));
//       assertEquals(testUser, userService.findByUserName("ram"));
//    }
//
//    @Test
//    @DisplayName("should throw Resource Not found exception, if username does not exists")
//    void findByUserName_throws_exception(){
//        when(userRepository.findByUserName("ramu"))
//                .thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> {
//            userService.findByUserName("ramu");
//        });
//    }
//}
