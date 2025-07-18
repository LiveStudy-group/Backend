package org.livestudy.service;

import org.livestudy.dto.UserLoginRequest;
import org.livestudy.dto.UserLoginResponse;
import org.livestudy.dto.UserSignupRequest;

public interface UserService {

    Long signup(UserSignupRequest request);

    UserLoginResponse login(UserLoginRequest request);
}
