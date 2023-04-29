package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping(value = "/{username}")
    public UserDto getUser(@PathVariable String username){
        return userService.getDtoByUsername(username);
    }
    @SneakyThrows
    @GetMapping(value = "/self")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDto getSelf(Principal principal){
        return userService.getFromPrincipal(principal);
    }
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto, Principal principal){
        return userService.changePassword(principal, passwordChangeDto);
    }
    @SneakyThrows
    @PostMapping("/edit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> editUser(@Valid @RequestBody UserEditDto userDto, Principal principal) {
        return userService.editUserInfo(principal, userDto);
    }
    @SneakyThrows
    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> setRole(@Valid @RequestBody ChangeRoleDto changeRoleDto) {
        return userService.setUserRole(changeRoleDto);
    }
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        return userService.deleteUser(username);
    }
}
