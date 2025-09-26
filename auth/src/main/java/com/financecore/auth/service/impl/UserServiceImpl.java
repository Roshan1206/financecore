package com.financecore.auth.service.impl;

import com.financecore.auth.dto.request.UserRegistrationRequest;
import com.financecore.auth.dto.response.UserInfoResponse;
import com.financecore.auth.entity.Role;
import com.financecore.auth.entity.User;
import com.financecore.auth.repository.RoleRepository;
import com.financecore.auth.repository.UserRepository;
import com.financecore.auth.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserInfoResponse createUser(UserRegistrationRequest userRegistrationRequest, String role) {
        Role userRole = roleRepository.findByRole(role);

        User user = new User();
        user.setFirstName(userRegistrationRequest.getFirstName());
        user.setLastName(userRegistrationRequest.getLastName());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);

        return new UserInfoResponse(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }
}
