package com.auth.app.controller;

import com.auth.app.dto.LoginDto;
import com.auth.app.dto.UserDto;
import com.auth.app.dto.UserRegistrationDto;
import com.auth.app.service.JwtService;
import com.auth.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String processLogin(@ModelAttribute("loginDto") LoginDto loginDto, BindingResult result, 
                              HttpServletResponse response, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            jwtService.setTokenInCookie(response, token);
            
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
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
    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        jwtService.clearTokenCookie(response);
        return "redirect:/auth/login?logout=true";
    }
}
