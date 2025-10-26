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

/**
 * Rest controller for managing unauthenticate users
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    /**
     * Injecting UserService
     */
    private final UserService userService;


    /**
     * Injecting required dependency via constructor injection
     */
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Creating new user with USER role
     */
    @PostMapping("/register/user")
    public ResponseEntity<UserInfoResponse> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest){
        UserInfoResponse response = userService.createUser(userRegistrationRequest, "USER");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Creating new user with ADMIN role
     */
    @PostMapping("/register/admin")
    public ResponseEntity<UserInfoResponse> registerAdmin(@RequestBody UserRegistrationRequest userRegistrationRequest){
        UserInfoResponse response = userService.createUser(userRegistrationRequest, "ADMIN");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
