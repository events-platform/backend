package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.repository.UserRepository;
import com.example.eventsplatformbackend.dto.UserDto;
import com.example.eventsplatformbackend.exceptions.UserNotFoundException;
import com.example.eventsplatformbackend.mapper.UserMapper;
import com.example.eventsplatformbackend.model.User;
import com.example.eventsplatformbackend.dto.UserCreationDto;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<String> saveUser(UserCreationDto userCreationDto){
        User userToSave = UserMapper.creationDtoToUser(userCreationDto);

        if (userRepository.existsByUsername(userToSave.getUsername())){
            return new ResponseEntity<>("User already exists", HttpStatus.OK);
        } else {
            userRepository.save(userToSave);
            return new ResponseEntity<>("User saved", HttpStatus.CREATED);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteUser(String username){

        if (username == null){
            return new ResponseEntity<>("Invalid username supplied", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByUsername(username)) {

            userRepository.deleteByUsername(username);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        } else {

            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public UserDto getUser(String username) throws InvalidParameterException, UserNotFoundException {

        if (username == null) {
            throw new InvalidParameterException("Username is null");
        }

        if (userRepository.existsByUsername(username)) {

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                return UserMapper.userToUserDto(user.get());
            }
        }

        throw new UserNotFoundException(String.format("Cannot find user with username %s", username));
    }

}
