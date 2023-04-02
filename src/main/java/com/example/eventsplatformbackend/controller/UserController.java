package com.example.eventsplatformbackend.controller;

import com.example.eventsplatformbackend.dto.RegistrationDto;
import com.example.eventsplatformbackend.dto.ChangeRoleDto;
import com.example.eventsplatformbackend.dto.LoginDto;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public User getUser(@PathVariable String username){
        return userService.findByUsername(username);
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
