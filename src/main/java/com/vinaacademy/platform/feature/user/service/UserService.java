package com.vinaacademy.platform.feature.user.service;

import com.vinaacademy.platform.feature.user.dto.UserDto;

public interface UserService {
    void createTestingData();

    UserDto getCurrentUser();
}
