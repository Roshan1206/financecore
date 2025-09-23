package com.financecore.auth.service;

import com.financecore.auth.dto.request.UserRegistrationRequest;
import com.financecore.auth.dto.response.UserInfoResponse;

public interface UserService {

    UserInfoResponse createUser(UserRegistrationRequest userRegistrationRequest, String role);
}
