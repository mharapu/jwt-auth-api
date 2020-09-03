package com.mirceah.jwtauthapi.service;

import com.mirceah.jwtauthapi.model.UserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Test
    void testNullUsername() {
        UserDetails userDetails = userService.getByUsername(null);
        assertNull(userDetails);
    }

    @Test
    void testNonExistingUser() {
        UserDetails userDetails = userService.getByUsername("test");
        assertNull(userDetails);
    }

    @Test
    void testExistingUser() {
        UserDetails userDetails = userService.getByUsername("user1");
        assertNotNull(userDetails);
    }

    @Test
    void testInvalidCredentials() {
        boolean result = userService.authenticate("user1", "passwordIncorrect");
        assertFalse(result);
    }

    @Test
    void testValidCredentials() {
        boolean result = userService.authenticate("user1", "pass1");
        assertTrue(result);
    }
}
