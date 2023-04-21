package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.exception.UserAlreadyExistsException;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.exception.WrongPasswordException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.domain.entity.User;
import com.google.common.io.Files;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${server.default-avatar-dir}")
    private String defaultAvatarDir;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileService fileService;

    public UserService(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.fileService = fileService;
        this.userMapper = userMapper;
    }

    public User createUser(RegistrationDto registrationDto) throws UserAlreadyExistsException {
        log.info("creating user {}", registrationDto.getUsername());
        User user = userMapper.registrationDtoToUser(registrationDto);

        if (userRepository.existsUserByUsername(user.getUsername())){
            throw new UserAlreadyExistsException(String.format("User with username %s already exists", user.getUsername()));
        } else if (userRepository.existsUserByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.getEmail()));
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("user {} saved", user.getUsername());

            return userRepository.getUserByUsername(user.getUsername());
        }
    }

    public User login(JwtRequest jwtRequest) throws WrongPasswordException, UserNotFoundException {
        User user = userRepository.findUserByEmail(jwtRequest.getEmail()).orElseThrow(() ->
                new UserNotFoundException(String.format("User with email %s not found", jwtRequest.getEmail())));

        if(bCryptPasswordEncoder.matches(jwtRequest.getPassword(), user.getPassword())){
            return user;
        } else {
            throw new WrongPasswordException("Wrong password");
        }
    }

    public ResponseEntity<String> changePassword(Principal principal, PasswordChangeDto passwordChangeDto){
        User user = userRepository.getUserByUsername(principal.getName());

        if(bCryptPasswordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())){
            log.info("user {} changed password", principal.getName());
            user.setPassword(bCryptPasswordEncoder.encode(passwordChangeDto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok().body("Password changed successfully");
        } else {
            return ResponseEntity.badRequest().body("Wrong old password");
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

        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Cannot find user with username %s", username)));
        return new UserDto(user);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public ResponseEntity<String> setUserRole(ChangeRoleDto changeRoleDto) throws UserNotFoundException {
        log.info("changing role of {} to {}", changeRoleDto.getUsername(), changeRoleDto.getRole());

        User user = userRepository.findUserByUsername(changeRoleDto.getUsername()).orElseThrow(() ->
                new UserNotFoundException(String.format("User %s not found", changeRoleDto.getUsername())));

        user.setRole(changeRoleDto.getRole());
        userRepository.save(user);
        return ResponseEntity.ok().body(String.format("%s id %s now", user.getUsername(), user.getRole()));
    }

    public ResponseEntity<UserDto> editUserInfo(Principal principal, UserEditDto dto) throws UserAlreadyExistsException {
        User user = userRepository.getUserByUsername(principal.getName());

        if(dto.getUsername() != null
                && !dto.getUsername().equals(user.getUsername())
                && userRepository.existsUserByUsername(dto.getUsername())){
            throw new UserAlreadyExistsException(String.format("User with username %s already exists", dto.getUsername()));
        }
        if(dto.getEmail() != null
                && !dto.getEmail().equals(user.getEmail())
                && userRepository.existsUserByEmail(dto.getEmail())){
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", dto.getEmail()));
        }
        if(dto.getPhone() != null
                && !dto.getPhone().equals(user.getPhone())
                && userRepository.existsUserByPhone(dto.getPhone())){
            throw new UserAlreadyExistsException(String.format("User with phone %s already exists", dto.getPhone()));
        }

        userMapper.updateUserFromUserEditDto(dto, user);
        userRepository.save(user);

        log.info("updated user {}", user.getUsername());
        return ResponseEntity.ok(new UserDto(user));
    }

    @SneakyThrows
    public ResponseEntity<String> setUserAvatar(MultipartFile uploadedFile, Principal principal){
        String pathToAvatar = fileService.saveUserAvatar(uploadedFile, principal.getName());

        User user = userRepository.getUserByUsername(principal.getName());
        String oldAvatar = user.getAvatar();
        if(oldAvatar != null && !oldAvatar.equals(pathToAvatar) && !oldAvatar.equals(fileService.getDefaultAvatarDirectory())) {
            fileService.deleteFile(oldAvatar);
        }
        user.setAvatar(pathToAvatar);
        userRepository.save(user);

        log.info("updated user {} avatar to {}", user.getUsername(), pathToAvatar);
        return ResponseEntity.status(201).build();
    }

    public ResponseEntity<InputStreamResource> getUserAvatar(Principal principal) throws FileNotFoundException, UnsupportedExtensionException, UserNotFoundException {
        String id = principal.getName();
        return getUserAvatarById(id);
    }

    public ResponseEntity<InputStreamResource> getUserAvatarById(String username) throws FileNotFoundException, UnsupportedExtensionException, UserNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("User %s not found", username)));
        String avatarPath = user.getAvatar();

        if (avatarPath == null){
            // if cannot found user image locally, use default image
            // for fix we need to use object storage
            avatarPath = defaultAvatarDir;
        }

        InputStream avatar = fileService.getFile(user.getAvatar());
        MediaType mediaType;
        switch (Files.getFileExtension(avatarPath)){
            case "png" -> mediaType = MediaType.IMAGE_PNG;
            case "jpeg", "jpg" -> mediaType = MediaType.IMAGE_JPEG;
            default -> throw new UnsupportedExtensionException(String.format("Unsupported content type for %s", avatarPath));
        }
        return ResponseEntity.ok().contentType(mediaType).body(new InputStreamResource(avatar));
    }
}
