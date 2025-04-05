package com.auth.app.controller;

import com.auth.app.dto.LoginDto;
import com.auth.app.dto.UserDto;
import com.auth.app.dto.UserRegistrationDto;
import com.auth.app.service.JwtService;
import com.auth.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("loginDto", new LoginDto());
        
        if (error != null) {
            model.addAttribute("error", "Invalid email or password.");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        
        return "login";
    }

    @PostMapping("/login-submit")
    @ResponseBody
    public ResponseEntity<?> processLogin(@RequestBody LoginDto loginDto) {
        try {
            // Add debug logging
            System.out.println("Attempting to authenticate user with email: " + loginDto.getEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            // Return JWT token
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Add specific error logging
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid email or password");
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                                     BindingResult result, Model model) {
        if (userService.emailExists(registrationDto.getEmail())) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        
        if (result.hasErrors()) {
            return "register";
        }
        
        userService.save(registrationDto);
        model.addAttribute("success", "Registration successful. Please login.");
        return "redirect:/auth/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/auth/login?logout=true";
    }
    
    // REST API for login
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> apiLogin(@RequestBody LoginDto loginDto) {
        return processLogin(loginDto);
    }
}
