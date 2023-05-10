package com.example.eventsplatformbackend.service.user;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.exception.UserAlreadyExistsException;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.exception.WrongPasswordException;
import com.example.eventsplatformbackend.mapper.PostMapper;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Является посредником между базой данных и контроллером. Выполняет CRUD операции над сущностью User.
 */
@Service
@Slf4j
public class UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PostMapper postMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

    public UserDto getDtoByUsername(String username) throws InvalidParameterException, UserNotFoundException {
        log.debug("getting user {}", username);

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

    public UserDto getFromPrincipal(Principal principal) {
        return new UserDto(userRepository.getUserByUsername(principal.getName()));
    }

    @Transactional
    public ResponseEntity<List<PostResponseDto>> getUserCreatedPosts(Principal principal) {
        User user = userRepository.getUserByUsername(principal.getName());
        List<PostResponseDto> posts = user.getCreatedPosts().stream()
                .map(postMapper::postDtoFromPost)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }
}