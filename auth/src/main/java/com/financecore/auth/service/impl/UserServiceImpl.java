package com.financecore.auth.service.impl;

import com.financecore.auth.dto.request.UserRegistrationRequest;
import com.financecore.auth.dto.response.UserInfoResponse;
import com.financecore.auth.entity.Role;
import com.financecore.auth.entity.User;
import com.financecore.auth.repository.RoleRepository;
import com.financecore.auth.repository.UserRepository;
import com.financecore.auth.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for handling users implementing UserService.
 *
 * @author Rosan
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * Storing encoded password
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Repository responsible for managing user roles
     */
    private final RoleRepository roleRepository;

    /**
     * Repository responsible for managing users.
     */
    private final UserRepository userRepository;


    /**
     * Injecting required dependency via constructor injection.
     *
     * @param passwordEncoder Password Encoder
     * @param roleRepository Role repository
     * @param userRepository User repository
     */
    public UserServiceImpl(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }


    /**
     * Registering new user with required info.
     *
     * @param userRegistrationRequest user info
     * @param role user role
     * @return Created user details
     */
    @Override
    public UserInfoResponse createUser(UserRegistrationRequest userRegistrationRequest, String role) {
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRole(role);
        roles.add(userRole);

        if (!"USER".equals(role)) {
            roles.add(roleRepository.findByRole("USER"));
        }

        User user = new User();
        user.setFirstName(userRegistrationRequest.getFirstName());
        user.setLastName(userRegistrationRequest.getLastName());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        return new UserInfoResponse(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }


    /**
     * Get authenticated user info
     *
     * @return UserInfo
     */
    @Override
    public UserInfoResponse getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return new UserInfoResponse(user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
