
package com.meetingscheduler.controller;

import com.meetingscheduler.auth.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthUserRepository authUserRepository;

    @GetMapping
    public List<UserResponse> all() {
        return authUserRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();
    }

    public record UserResponse(
            Long id,
            String name,
            String email,
            String role
    ) {}
}
