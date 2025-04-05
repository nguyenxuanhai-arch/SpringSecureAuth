package com.auth.app.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String getUsernameFromToken(String token);
    boolean validateToken(String token, UserDetails userDetails);
}
