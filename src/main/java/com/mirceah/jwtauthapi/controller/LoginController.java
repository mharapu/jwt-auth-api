package com.mirceah.jwtauthapi.controller;

import com.mirceah.jwtauthapi.model.AuthRequest;
import com.mirceah.jwtauthapi.model.JwtResponse;
import com.mirceah.jwtauthapi.model.UserDetails;
import com.mirceah.jwtauthapi.service.JwtTokenUtil;
import com.mirceah.jwtauthapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(path = "/login")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthRequest authRequest) {
        if (!userService.authenticate(authRequest.getUsername(), authRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponse("invalid"));
        }
        final UserDetails userDetails = this.userService.getByUsername(authRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
