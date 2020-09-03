package com.mirceah.jwtauthapi.controller;

import com.mirceah.jwtauthapi.model.UserDetails;
import com.mirceah.jwtauthapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/user/details")
    UserDetails getDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getByUsername(userDetails.getUsername());
    }
}
