package com.example.springboot.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springboot.dtos.AuthenticationDto;
import com.example.springboot.dtos.LoginResponseDto;
import com.example.springboot.dtos.RegisterDto;
import com.example.springboot.models.UserModel;
import com.example.springboot.repositories.UserRepository;
import com.example.springboot.services.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;
    
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthenticationDto authenticationDto) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationDto.username(), authenticationDto.password());
        var auth = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        var token = tokenService.generateToken((UserModel) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterDto registerDto) {
        if (this.userRepository.findByUsername(registerDto.username()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        
            String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());
            UserModel newUser = new UserModel(registerDto.username(), encryptedPassword, registerDto.role());

            this.userRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.OK).build();
    }
}
