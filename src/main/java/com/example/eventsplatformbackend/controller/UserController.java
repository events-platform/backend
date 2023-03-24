package com.example.eventsplatformbackend.controller;

import com.example.eventsplatformbackend.dto.UserCreationDto;
import com.example.eventsplatformbackend.dto.UserDto;
import com.example.eventsplatformbackend.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "user")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserCreationDto userCreationDto){
        log.info("creating user {}", userCreationDto.toString());
        return userService.saveUser(userCreationDto);
    }

    @SneakyThrows
    @GetMapping(value = "/{username}")
    public UserDto getUser(@PathVariable String username){
        log.info("getting user {}", username);
        return userService.getUser(username);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        log.info("deleting user {}", username);
        return userService.deleteUser(username);
    }
}
