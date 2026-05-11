package com.example.demo.shared.services;

import com.example.demo.shared.dto.UserDto;

import java.util.List;

public interface UserQueryService {
    UserDto findById(Long id);
    List<UserDto> searchByUsername(String query);
}
