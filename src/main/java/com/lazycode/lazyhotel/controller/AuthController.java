package com.lazycode.lazyhotel.controller;


import com.lazycode.lazyhotel.exception.UserAlreadyExistsException;
import com.lazycode.lazyhotel.model.User;
import com.lazycode.lazyhotel.service.IUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;


    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(User user) {
        try{
            userService.registerUser(user);
            return ResponseEntity.ok("Registered Successfully!");
        }catch(UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }
    }
}
