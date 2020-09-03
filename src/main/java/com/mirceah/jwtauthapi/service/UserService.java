package com.mirceah.jwtauthapi.service;

import com.mirceah.jwtauthapi.model.UserDetails;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class UserService {

    private LinkedList<UserDetails> users;

    public UserService() {
        users = new LinkedList<>();
        users.add(new UserDetails("user1", "pass1", "admin"));
        users.add(new UserDetails("user2", "pass2", "user"));
    }

    public UserDetails getByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    public boolean authenticate(String username, String password) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(password));
    }
}
