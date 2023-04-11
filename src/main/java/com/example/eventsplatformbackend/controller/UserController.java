package com.example.eventsplatformbackend.controller;

import com.example.eventsplatformbackend.dto.RegistrationDto;
import com.example.eventsplatformbackend.dto.ChangeRoleDto;
import com.example.eventsplatformbackend.dto.LoginDto;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public User getUser(@PathVariable String username){
        return userService.getByUsername(username);
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal){
        return userService.setUserAvatar(uploadedFile, principal);
    }
    @SneakyThrows
    @GetMapping(value = "/avatar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> getAvatar(Principal principal){
        return userService.getUserAvatar(principal);
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
