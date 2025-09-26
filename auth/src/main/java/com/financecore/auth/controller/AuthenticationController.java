package com.financecore.auth.controller;

import com.financecore.auth.dto.request.UserRegistrationRequest;
import com.financecore.auth.dto.response.UserInfoResponse;
import com.financecore.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserInfoResponse> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest){
        UserInfoResponse response = userService.createUser(userRegistrationRequest, "USER");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
