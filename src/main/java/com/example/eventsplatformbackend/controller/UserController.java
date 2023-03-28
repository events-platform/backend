package com.example.eventsplatformbackend.controller;

import com.example.eventsplatformbackend.config.JwtUtil;
import com.example.eventsplatformbackend.dto.ChangeRoleDto;
import com.example.eventsplatformbackend.dto.LoginDto;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "user")
@Slf4j
public class UserController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public UserController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody LoginDto loginDto){

        if (userService.registerUser(loginDto)){
            User user = userService.findByUsername(loginDto.getUsername());
            return ResponseEntity.status(201).body(jwtUtil.generateToken(user));
        } else {
            return ResponseEntity.badRequest().body("user already exists");
        }
    }

    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto){
        log.info("user {} logging in", loginDto.getUsername());

        User user = userService.findByUsername(loginDto.getUsername());
        return ResponseEntity.ok().body(jwtUtil.generateToken(user));
    }

    @PostMapping("/role")
    public ResponseEntity<String> setRole(@Valid @RequestBody ChangeRoleDto changeRoleDto){
        log.info("changing role of {} to {}", changeRoleDto.getUsername(), changeRoleDto.getRole());
        return userService.setUserRole(changeRoleDto);
    }

    @SneakyThrows
    @GetMapping(value = "/{username}")
    public User getUser(@PathVariable String username){
        log.info("getting user {}", username);
        return userService.findByUsername(username);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        log.info("deleting user {}", username);
        return userService.deleteUser(username);
    }
}
