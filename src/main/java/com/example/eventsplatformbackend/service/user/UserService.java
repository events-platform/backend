package com.example.eventsplatformbackend.service.user;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.exception.UserAlreadyExistsException;
import com.example.eventsplatformbackend.exception.UserNotFoundException;
import com.example.eventsplatformbackend.exception.WrongPasswordException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

/**
 * Работает с личной информацией пользователя
 */
@Service
@Slf4j
public class UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    public User createUser(RegistrationDto registrationDto) throws UserAlreadyExistsException {
        log.info("creating user {}", registrationDto.getUsername());
        User user = userMapper.registrationDtoToUser(registrationDto);

        if (userRepository.existsUserByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        } else if (userRepository.existsUserByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("user {} saved", user.getUsername());

            return userRepository.getUserByUsername(user.getUsername());
        }
    }

    public User login(JwtRequest jwtRequest) throws WrongPasswordException, UserNotFoundException {
        User user = userRepository.findUserByEmail(jwtRequest.getEmail()).orElseThrow(() ->
                new UserNotFoundException("Пользователя с такой почтой не существует"));

        if(bCryptPasswordEncoder.matches(jwtRequest.getPassword(), user.getPassword())){
            return user;
        } else {
            throw new WrongPasswordException("Неверно введен пароль");
        }
    }

    public ResponseEntity<String> changePassword(Principal principal, PasswordChangeDto passwordChangeDto){
        User user = userRepository.getUserByUsername(principal.getName());

        if(bCryptPasswordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())){
            log.info("user {} changed password", principal.getName());
            user.setPassword(bCryptPasswordEncoder.encode(passwordChangeDto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok().body("Вы успешно сменили пароль");
        } else {
            return ResponseEntity.badRequest().body("Неправильно введен старый пароль");
        }
    }

    @Transactional
    public ResponseEntity<String> deleteUser(String username){
        log.info("deleting user {}", username);

        if (userRepository.existsUserByUsername(username)) {
            userRepository.deleteUserByUsername(username);
            return ResponseEntity.ok("Пользователь удален");
        } else {
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
    }

    public UserDto getDtoByUsername(String username) throws UserNotFoundException {
        log.debug("getting user {}", username);

        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователя с именем %s не существует", username)));
        return new UserDto(user);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public ResponseEntity<String> setUserRole(ChangeRoleDto changeRoleDto) throws UserNotFoundException {
        log.info("changing role of {} to {}", changeRoleDto.getUsername(), changeRoleDto.getRole());

        User user = userRepository.findUserByUsername(changeRoleDto.getUsername()).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь %s не найден", changeRoleDto.getUsername())));

        user.setRole(changeRoleDto.getRole());
        userRepository.save(user);
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body(String.format("%s теперь %s ", user.getUsername(), user.getRole()));
    }
    public ResponseEntity<UserDto> editUserInfo(Principal principal, UserEditDto dto) throws UserAlreadyExistsException {
        User user = userRepository.getUserByUsername(principal.getName());

        if(dto.getUsername() != null
                && !dto.getUsername().equals(user.getUsername())
                && userRepository.existsUserByUsername(dto.getUsername())){
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        if(dto.getEmail() != null
                && !dto.getEmail().equals(user.getEmail())
                && userRepository.existsUserByEmail(dto.getEmail())){
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        }
        if(dto.getPhone() != null
                && !dto.getPhone().equals(user.getPhone())
                && userRepository.existsUserByPhone(dto.getPhone())){
            throw new UserAlreadyExistsException("Пользователь с таким номером телефона уже существует");
        }

        userMapper.updateUserFromUserEditDto(dto, user);
        userRepository.save(user);

        log.info("updated user {}", user.getUsername());
        return ResponseEntity.ok(new UserDto(user));
    }

    public UserDto getDtoFromPrincipal(Principal principal) {
        return new UserDto(userRepository.getUserByUsername(principal.getName()));
    }
}