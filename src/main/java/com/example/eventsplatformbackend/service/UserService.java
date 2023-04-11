package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.dto.ChangeRoleDto;
import com.example.eventsplatformbackend.dto.RegistrationDto;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import com.example.eventsplatformbackend.repository.UserRepository;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.dto.LoginDto;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.google.common.io.Files;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
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

    public User getByUsername(String username) throws InvalidParameterException, UserNotFoundException {
        log.info("getting user {}", username);

        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        throw new UserNotFoundException(String.format("Cannot find user with username %s", username));
    }

    public ResponseEntity<String> setUserRole(ChangeRoleDto changeRoleDto){
        log.info("changing role of {} to {}", changeRoleDto.getUsername(), changeRoleDto.getRole());

        Optional<User> optionalUser = userRepository.findUserByUsername(changeRoleDto.getUsername());
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

        Optional<User> user = userRepository.findUserByUsername(loginDto.getUsername());
        if(user.isEmpty()){
            throw new UserNotFoundException(String.format("user %s not found", loginDto.getUsername()));
        }

        if(bCryptPasswordEncoder.matches(loginDto.getPassword(), user.get().getPassword())){
            return ResponseEntity.ok().body(jwtUtil.generateToken(user.get()));
        } else {
            return ResponseEntity.badRequest().body("wrong password");
        }
    }

    @SneakyThrows
    public ResponseEntity<String> setUserAvatar(MultipartFile uploadedFile, Principal principal){
        String pathToAvatar = fileService.saveUserAvatar(uploadedFile, principal.getName());

        User user = userRepository.getUserByUsername(principal.getName());
        String oldAvatar = user.getAvatar();
        if(oldAvatar != null && !oldAvatar.equals(pathToAvatar)) {
            fileService.deleteFile(oldAvatar);
        }
        user.setAvatar(pathToAvatar);
        userRepository.save(user);

        log.info("updated user {} avatar to {}", user.getUsername(), pathToAvatar);
        return ResponseEntity.status(201).build();
    }

    public ResponseEntity<InputStreamResource> getUserAvatar(Principal principal) throws FileNotFoundException, UnsupportedExtensionException {
        String username = principal.getName();
        User user = userRepository.getUserByUsername(username);
        String avatarPath = user.getAvatar();

        if (avatarPath == null){
            throw new FileNotFoundException(String.format("user %s does not have avatar", username));
        }
        InputStream avatar = fileService.getFile(user.getAvatar());
        MediaType mediaType;
        switch (Files.getFileExtension(avatarPath)){
            case "png" -> mediaType = MediaType.IMAGE_PNG;
            case "jpeg", "jpg" -> mediaType = MediaType.IMAGE_JPEG;
            default -> throw new UnsupportedExtensionException(String.format("unsupported content type for %s", avatarPath));
        }
        return ResponseEntity.ok().contentType(mediaType).body(new InputStreamResource(avatar));
    }
}
