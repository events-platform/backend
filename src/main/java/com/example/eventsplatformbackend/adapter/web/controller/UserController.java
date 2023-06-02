package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @GetMapping(value = "/{username}", produces = "application/json; charset=utf-8")
    public UserDto getUser(@PathVariable String username){
        return userService.getDtoByUsername(username);
    }
    @GetMapping(value = "/self", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDto getSelf(Principal principal){
        return userService.getDtoFromPrincipal(principal);
    }
    @PutMapping(value = "/change-password", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto, Principal principal){
        return userService.changePassword(principal, passwordChangeDto);
    }
    @PostMapping(value = "/edit", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDto editUser(@Valid @RequestBody UserEditDto userDto, Principal principal) {
        return userService.editUserInfo(principal, userDto);
    }
    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String setRole(@Valid @RequestBody ChangeRoleDto changeRoleDto) {
        return userService.setUserRole(changeRoleDto);
    }
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable String username){
        return userService.deleteUser(username);
    }
}
