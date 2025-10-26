package com.financecore.auth.controller;

import com.financecore.auth.dto.response.UserInfoResponse;
import com.financecore.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for authenticated users for managing profiles.
 *
 * @author Roshan
 */
@RestController
@RequestMapping("/v1/user")
public class UserController {

    /**
     * Service class for User operaions.
     */
    private final UserService userService;

    /**
     * Injecting required dependency via constructor injection.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Get authenticate user info
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        UserInfoResponse response = userService.getUserInfo();
        return ResponseEntity.ok(response);
    }
}
