package com.financecore.auth.service.impl;

import com.financecore.auth.dto.request.UserRegistrationRequest;
import com.financecore.auth.dto.response.UserInfoResponse;
import com.financecore.auth.repository.RoleRepository;
import com.financecore.auth.repository.UserRepository;
import com.financecore.auth.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserInfoResponse createUser(UserRegistrationRequest userRegistrationRequest, String role) {
        return null;
    }
}
