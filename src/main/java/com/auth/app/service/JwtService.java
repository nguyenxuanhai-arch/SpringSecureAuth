package com.auth.app.service;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean validateToken(String token, UserDetails userDetails);
    void setTokenInCookie(HttpServletResponse response, String token);
    String getTokenFromCookie(HttpServletRequest request);
    void clearTokenCookie(HttpServletResponse response);
}
