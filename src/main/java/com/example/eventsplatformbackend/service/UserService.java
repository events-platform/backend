package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.mapper.UserMapperImpl;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.google.common.io.Files;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserMapperImpl userMapperImpl;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final FileService fileService;

    public UserService(UserRepository userRepository, UserMapperImpl userMapperImpl, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, FileService fileService) {
        this.userRepository = userRepository;
        this.userMapperImpl = userMapperImpl;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.fileService = fileService;
        this.userMapper = Mappers.getMapper(UserService.getClass());
    }

    public ResponseEntity<String> createUser(RegistrationDto registrationDto){
        log.info("creating user {}", registrationDto.getUsername());
        User user = new User();
        userMapper.createUserFromRegistrationDto(registrationDto, user);

        if (userRepository.existsUserByUsername(user.getUsername())){

            log.info("user with username {} already exists", user.getUsername());
            return ResponseEntity
                    .status(409)
                    .body(String.format("user with username %s already exists", user.getUsername()));
        } else if (userRepository.existsUserByEmail(user.getEmail())){

            log.info("user with email {} already exists", user.getEmail());
            return ResponseEntity
                    .status(409)
                    .body(String.format("user with email %s already exists", user.getEmail()));
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("user {} saved", user.getUsername());
            return ResponseEntity
                    .status(201).body(jwtUtil.generateToken(user));
        }
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

    public ResponseEntity<String> changePassword(Principal principal, PasswordChangeDto passwordChangeDto){
        User user = userRepository.getUserByUsername(principal.getName());

        if(bCryptPasswordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())){
            log.info("user {} changed password", principal.getName());
            user.setPassword(bCryptPasswordEncoder.encode(passwordChangeDto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok().body("password changed successfully");
        } else {
            return ResponseEntity.badRequest().body("wrong old password");
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

    public UserDto getByUsername(String username) throws InvalidParameterException, UserNotFoundException {
        log.info("getting user {}", username);

        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            return new UserDto(optionalUser.get());
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

    public ResponseEntity<UserDto> editUserInfo(Principal principal, UserEditDto userEditDto){
        User user = userRepository.getUserByUsername(principal.getName());

        userMapper.updateUserFromDto(userEditDto, user);
        userRepository.save(user);

        log.info("updated user {}", user.getUsername());
        return ResponseEntity.ok(new UserDto(user));
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

    public ResponseEntity<InputStreamResource> getUserAvatar(Principal principal) throws FileNotFoundException, UnsupportedExtensionException, UserNotFoundException {
        String username = principal.getName();
        return getUserAvatarByUsername(username);
    }

    public ResponseEntity<InputStreamResource> getUserAvatarByUsername(String username) throws FileNotFoundException, UnsupportedExtensionException, UserNotFoundException {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException(String.format("user %s not found", username));
        }
        User user = optionalUser.get();
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
