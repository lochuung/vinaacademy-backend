package com.vinaacademy.platform.feature.user.service;

import com.vinaacademy.platform.feature.user.dto.UpdateUserInfoRequest;
import com.vinaacademy.platform.feature.user.dto.UserDto;

public interface UserService {
    void createTestingData();

    UserDto getCurrentUser();
    
    UserDto updateUserInfo(UpdateUserInfoRequest request);
}
