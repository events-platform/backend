package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PasswordChangeDto;
import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.ChangeRoleDto;
import com.example.eventsplatformbackend.domain.dto.request.LoginDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "user")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody RegistrationDto registrationDto){
        return userService.createUser(registrationDto);
    }
    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
    @SneakyThrows
    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDto getUser(@PathVariable String username){
        return userService.getByUsername(username);
    }
    @SneakyThrows
    @GetMapping(value = "/{username}/avatar")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable String username){
        return userService.getUserAvatarByUsername(username);
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto, Principal principal){
        return userService.changePassword(principal, passwordChangeDto);
    }
    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> setRole(@Valid @RequestBody ChangeRoleDto changeRoleDto){
        return userService.setUserRole(changeRoleDto);
    }
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        return userService.deleteUser(username);
    }
}
