package com.mirceah.jwtauthapi.service;

import com.mirceah.jwtauthapi.model.UserDetails;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtTokenUtilTests {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private UserDetails userDetails;
    private String token;

    @BeforeAll
    void initTests() {
        userDetails = new UserDetails("test", "test", "user");
        token = jwtTokenUtil.generateToken(userDetails);
    }

    @Test
    void testGetUsernameFromToken() {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void testGetExpirationDateFromToken() {
        Date date = jwtTokenUtil.getExpirationDateFromToken(token);
        assertNotNull(date);
    }

    @Test
    void testTokenIsNotExpired() {
        Date date = jwtTokenUtil.getExpirationDateFromToken(token);
        assertTrue(new Date().before(date));
        assertTrue(new Date().getTime() + jwtTokenUtil.JWT_TOKEN_VALIDITY + 1 > date.getTime());
    }

    @Test
    void testTokenIsValid() {
        boolean result = jwtTokenUtil.validateToken(token, userDetails);
        assertTrue(result);
    }

    @Test
    void testTokenIsInvalid() {
        String token2 = jwtTokenUtil.generateToken(new UserDetails("test2", "test2", "admin"));
        boolean result = jwtTokenUtil.validateToken(token2, userDetails);
        assertFalse(result);
        result = jwtTokenUtil.validateToken(token, new UserDetails("test2", "test2", "admin"));
        assertFalse(result);
    }
}
