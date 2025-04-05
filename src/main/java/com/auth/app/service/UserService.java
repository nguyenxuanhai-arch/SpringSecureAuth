package com.auth.app.service;

import com.auth.app.dto.UserDto;
import com.auth.app.dto.UserRegistrationDto;
import com.auth.app.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    UserDto findByEmail(String email);
    UserDto getCurrentUser();
    boolean emailExists(String email);
}
