package com.financecore.auth.service;

import com.financecore.auth.dto.request.UserRegistrationRequest;
import com.financecore.auth.dto.response.UserInfoResponse;

/**
 * Interface handling all user operations
 *
 * @author Roshan
 */
public interface UserService {

    /**
     * Registering new user with required info.
     *
     * @param userRegistrationRequest user info
     * @param role user role
     *
     * @return Created user details
     */
    UserInfoResponse createUser(UserRegistrationRequest userRegistrationRequest, String role);


    /**
     * Get authenticated user info
     *
     * @return UserInfo
     */
    UserInfoResponse getUserInfo();
}
