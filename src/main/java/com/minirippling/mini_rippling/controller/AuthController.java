package com.minirippling.mini_rippling.controller;

import com.minirippling.mini_rippling.domain.UserAccount;
import com.minirippling.mini_rippling.dto.LoginRequest;
import com.minirippling.mini_rippling.dto.RegisterRequest;
import com.minirippling.mini_rippling.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService ){
        this.authService=authService;

    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/register")
    public UserAccount register(@RequestBody RegisterRequest request) {
        return authService.register(
                request.getEmployeeId(),
                request.getPassword());
    }

}
