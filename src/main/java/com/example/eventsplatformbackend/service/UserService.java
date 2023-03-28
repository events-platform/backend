package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.dto.ChangeRoleDto;
import com.example.eventsplatformbackend.repository.UserRepository;
import com.example.eventsplatformbackend.exceptions.UserNotFoundException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.dto.LoginDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public boolean registerUser(LoginDto loginDto){
        log.info("creating user {}", loginDto.getUsername());

        User userToSave = UserMapper.creationDtoToUser(loginDto);

        if (userRepository.existsByUsername(userToSave.getUsername())){
            log.info("user {} already exists", userToSave.getUsername());
            return false;
        } else {
            userToSave.setPassword(bCryptPasswordEncoder.encode(userToSave.getPassword()));
            userRepository.save(userToSave);
            log.info("user {} saved", userToSave.getUsername());
            return true;
        }
    }

    @Transactional
    public ResponseEntity<String> deleteUser(String username){

        if (username == null){
            return new ResponseEntity<>("Invalid username supplied", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByUsername(username)) {
            userRepository.deleteUserByUsername(username);
            return ResponseEntity.ok("User deleted");
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public User findByUsername(String username) throws InvalidParameterException, UserNotFoundException {

        if (username == null) {
            throw new InvalidParameterException("Username is null");
        }

        if (userRepository.existsByUsername(username)) {
            Optional<User> user = userRepository.findByUsername(username);
            return user.get();
        }

        throw new UserNotFoundException(String.format("Cannot find user with username %s", username));
    }

    public ResponseEntity<String> setUserRole(ChangeRoleDto changeRoleDto){

        if (!userRepository.existsByUsername(changeRoleDto.getUsername())){
            return ResponseEntity.badRequest().body(
                    String.format("User with username %s not found", changeRoleDto.getUsername()));
        }

        User user = userRepository.findByUsername(changeRoleDto.getUsername()).get();
        user.setRole(changeRoleDto.getRole());
        userRepository.save(user);

        return ResponseEntity.ok().body(String.format("%s id %s now", user.getUsername(), user.getRole()));
    }

}
