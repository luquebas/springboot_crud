package com.example.springboot.dtos;
import com.example.springboot.models.enums.UserRole;

public record RegisterDto(String username, String password, UserRole role) {
    
}
