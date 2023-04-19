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

    public User login(JwtRequest jwtRequest) throws WrongPasswordException {
        log.info("user {} logging in", jwtRequest.getEmail());

        User user = userRepository.getUserByEmail(jwtRequest.getEmail());
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

        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            return new UserDto(optionalUser.get());
        }
        throw new UserNotFoundException(String.format("Cannot find user with username %s", username));
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
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

        userMapper.updateUserFromUserEditDto(userEditDto, user);
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
            throw new UserNotFoundException(String.format("User %s not found", username));
        }
        User user = optionalUser.get();
        String avatarPath = user.getAvatar();

        if (avatarPath == null){
            throw new FileNotFoundException(String.format("User %s does not have avatar", username));
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
