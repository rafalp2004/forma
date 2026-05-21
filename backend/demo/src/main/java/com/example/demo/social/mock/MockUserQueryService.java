package com.example.demo.social.mock;

import com.example.demo.shared.dto.UserDto;
import com.example.demo.shared.services.UserQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockUserQueryService implements UserQueryService {

    @Override
    public UserDto findById(Long id) {
        return new UserDto(id, "user_" + id, "user" + id + "@example.com");
    }

    @Override
    public List<UserDto> searchByUsername(String query) {
        return List.of(
                new UserDto(1L, "rafal", "rafal@example.com"),
                new UserDto(2L, "arek", "arek@example.com"),
                new UserDto(3L, "mateusz", "mateusz@example.com")
        );
    }
}
