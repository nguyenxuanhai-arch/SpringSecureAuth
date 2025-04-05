package com.auth.app.service.impl;

import com.auth.app.config.JwtTokenUtil;
import com.auth.app.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String JWT_COOKIE_NAME = "JWT_TOKEN";
    private static final int COOKIE_MAX_AGE = 86400; // 1 day in seconds

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public String generateToken(UserDetails userDetails) {
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public String extractUsername(String token) {
        return jwtTokenUtil.extractUsername(token);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        return jwtTokenUtil.validateToken(token, userDetails);
    }

    @Override
    public void setTokenInCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        // cookie.setSecure(true); // Enable in production with HTTPS
        response.addCookie(cookie);
    }

    @Override
    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
                
        return jwtCookie.map(Cookie::getValue).orElse(null);
    }

    @Override
    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
