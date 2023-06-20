package com.example.eventsplatformbackend.service.user;

import com.example.eventsplatformbackend.domain.dto.request.*;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.common.exception.UserAlreadyExistsException;
import com.example.eventsplatformbackend.common.exception.UserNotFoundException;
import com.example.eventsplatformbackend.common.exception.WrongPasswordException;
import com.example.eventsplatformbackend.common.mapper.UserMapper;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Выполняет операции над сущностью User
 * Может создавать, редактировать и доставать из базы пользователей
 * Также может быть использован другими классами как обертка над UserRepository
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
        log.info("Creating user {}", registrationDto.getUsername());
        User user = userMapper.registrationDtoToUser(registrationDto);

        if (userRepository.existsUserByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        } else if (userRepository.existsUserByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("User {} saved", user.getUsername());

            return userRepository.getUserByUsername(user.getUsername());
        }
    }

    public User login(JwtRequest jwtRequest) throws WrongPasswordException {
        User user = userRepository.findUserByEmail(jwtRequest.getEmail()).orElseThrow(() ->
                new UsernameNotFoundException("Пользователя с такой почтой не существует"));

        if(bCryptPasswordEncoder.matches(jwtRequest.getPassword(), user.getPassword())){
            return user;
        } else {
            throw new WrongPasswordException("Неверно введен пароль");
        }
    }

    public ResponseEntity<String> changePassword(Principal principal, PasswordChangeDto passwordChangeDto){
        User user = userRepository.getUserByUsername(principal.getName());

        if(bCryptPasswordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())){
            log.info("User {} changed password", principal.getName());
            user.setPassword(bCryptPasswordEncoder.encode(passwordChangeDto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok().body("Вы успешно сменили пароль");
        } else {
            return ResponseEntity.badRequest().body("Неправильно введен старый пароль");
        }
    }
    @Transactional
    public String deleteUser(String username){
        log.info("Deleting user {}", username);
        User userToDelete = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователя с именем %s не существует", username)));
        log.info("Deleted user {}", userToDelete.getUsername());
        userRepository.delete(userToDelete);
        return "Пользователь удален";
    }

    public UserDto getDtoByUsername(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователя с именем %s не существует", username)));
        return userMapper.userToUserDto(user);
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public String setUserRole(ChangeRoleDto changeRoleDto) throws UserNotFoundException {
        log.info("Changing role of {} to {}", changeRoleDto.getUsername(), changeRoleDto.getRole());

        User user = userRepository.findUserByUsername(changeRoleDto.getUsername()).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь %s не найден", changeRoleDto.getUsername())));

        user.setRole(changeRoleDto.getRole());
        userRepository.save(user);
        return String.format("%s теперь %s ", user.getUsername(), user.getRole());
    }
    public UserDto editUserInfo(Principal principal, UserEditDto dto) {
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

        log.info("Update user {}", user.getUsername());
        return userMapper.userToUserDto(user);
    }

    public UserDto getDtoFromPrincipal(Principal principal) {
        User user = userRepository.getUserByUsername(principal.getName());
        return userMapper.userToUserDto(user);
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    public Optional<User> findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }
    public User getUserByUsername(String username){
        return userRepository.getUserByUsername(username);
    }
    public List<User> getPostSubscribers(Post post){
        return userRepository.getUsersBySubscribedPosts(post);
    }
}