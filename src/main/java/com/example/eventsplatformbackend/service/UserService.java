package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.dto.ChangeRoleDto;
import com.example.eventsplatformbackend.dto.RegistrationDto;
import com.example.eventsplatformbackend.repository.UserRepository;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.dto.LoginDto;
import com.example.eventsplatformbackend.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.Optional;

@Service
@Slf4j
public class UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final FileService fileService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, FileService fileService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.fileService = fileService;
    }

    public ResponseEntity<String> createUser(RegistrationDto registrationDto){
        log.info("creating user {}", registrationDto.getUsername());

        User userToSave = UserMapper.registrationDtoToUser(registrationDto);

        if (userRepository.existsUserByUsername(userToSave.getUsername())){
            log.info("user with username {} already exists", userToSave.getUsername());

            return ResponseEntity
                    .status(409)
                    .body(String.format("user with username %s already exists", userToSave.getUsername()));
        } else if (userRepository.existsUserByEmail(userToSave.getEmail())){
            log.info("user with email {} already exists", userToSave.getEmail());

            return ResponseEntity
                    .status(409)
                    .body(String.format("user with email %s already exists", userToSave.getEmail()));
        } else {
            userToSave.setPassword(bCryptPasswordEncoder.encode(userToSave.getPassword()));
            userRepository.save(userToSave);
            log.info("user {} saved", userToSave.getUsername());

            return ResponseEntity
                    .status(201).body(jwtUtil.generateToken(userToSave));
        }
    }

    @Transactional
    public ResponseEntity<String> deleteUser(String username){
        log.info("deleting user {}", username);

        if (userRepository.existsUserByUsername(username)) {
            userRepository.deleteUserByUsername(username);
            return ResponseEntity.ok("User deleted");
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public User findByUsername(String username) throws InvalidParameterException, UserNotFoundException {
        log.info("getting user {}", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        throw new UserNotFoundException(String.format("Cannot find user with username %s", username));
    }

    public ResponseEntity<String> setUserRole(ChangeRoleDto changeRoleDto){
        log.info("changing role of {} to {}", changeRoleDto.getUsername(), changeRoleDto.getRole());

        Optional<User> optionalUser = userRepository.findByUsername(changeRoleDto.getUsername());
        if (optionalUser.isEmpty()){
            return ResponseEntity.status(404).body(
                    String.format("User with username %s not found", changeRoleDto.getUsername()));
        }

        User user = optionalUser.get();
        user.setRole(changeRoleDto.getRole());
        userRepository.save(user);

        return ResponseEntity.ok().body(String.format("%s id %s now", user.getUsername(), user.getRole()));
    }

    public ResponseEntity<String> login(LoginDto loginDto) throws UserNotFoundException {
        log.info("user {} logging in", loginDto.getUsername());

        User user = this.findByUsername(loginDto.getUsername());

        if(bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            return ResponseEntity.ok().body(jwtUtil.generateToken(user));
        } else {
            return ResponseEntity.badRequest().body("wrong password!");
        }
    }

    @SneakyThrows
    public ResponseEntity<String> uploadUserAvatar(MultipartFile uploadedFile, Principal principal){
        String pathToFile = fileService.saveUserAvatar(uploadedFile, principal);

        // не проверяем на null т.к. у нас есть principal, значит юзер есть в бд
        User user = userRepository.findByUsername(principal.getName()).get();
        String oldAvatar = user.getAvatar();
        if(oldAvatar != null) {
            fileService.deleteFile(oldAvatar);
        }

        user.setAvatar(pathToFile);
        userRepository.save(user);
        log.info("updated user {} avatar to {}", user.getUsername(), pathToFile);

        return ResponseEntity.status(201).build();
    }
}
