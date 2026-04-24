package com.example.server.service;

import com.example.server.config.TransactionManager;
import com.example.server.entity.User;
import com.example.server.exception.NotFoundException;
import com.example.server.repository.implementations.UserRepositoryImpl;
import com.example.server.repository.interfaces.UserRepository;
import com.example.server.security.PasswordService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserCreateDto;
import org.example.dto.user.UserDto;
import org.example.dto.user.UserUpdateDto;
import org.example.enums.Role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private final PasswordService passwordService;

    public UserDto createUser(UserCreateDto dto){
       return TransactionManager.executeInTransaction(em -> {
        if(userRepository.findByEmail(dto.getEmail(),em) != null){
            throw new ValidationException("User with this email is already exists!");
        }
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordService.hash(dto.getPassword()));
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.ROLE_USER);

        User saved = userRepository.saveOrUpdate(user, em);
        return toDto(saved);
       });
    }

    public UserDto registerUser(UserCreateDto dto)
    {
        return TransactionManager.executeInTransaction(em->{
            if(userRepository.findByEmail(dto.getEmail(), em) != null){
                throw new ValidationException("User with this email is already exists");
            }

            User user = new User();

            user.setName(dto.getName());
            user.setPassword(passwordService.hash(dto.getPassword()));
            user.setRole(Role.ROLE_USER);
            user.setEmail(dto.getEmail());

            User saved = userRepository.saveOrUpdate(user, em);
            return toDto(saved);
        });
    }
    public User authenticate(String email, String password){
        return TransactionManager.executeInTransaction(em -> {
            User user = userRepository.findByEmail(email, em);

            if(user == null){
                return null;
            }

            if (!passwordService.matches(password, user.getPassword())) {
                return null;
            }

            return user;
        });
    }

    public UserDto getUser(Long id) {
        return TransactionManager.executeInTransaction(em ->{
            return userRepository.findById(id, em)
                    .map(this::toDto)
                    .orElseThrow(()->new NotFoundException("User not found with id" + id));
        });
    }

    public List<UserDto> getAllUsers() {
        return TransactionManager.executeInTransaction(em -> {
            return userRepository.findAll(em).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        });
    }

    public UserDto updateUser(Long id, UserUpdateDto dto) {
        return TransactionManager.executeInTransaction(em -> {
            User user = userRepository.findById(id, em)
                    .orElseThrow(()->new NotFoundException("There is no such user with id " + id));

            if(!user.getEmail().equals(dto.getEmail()) &&
                    userRepository.findByEmail(dto.getEmail(),em) != null){
                throw new NotFoundException("Email already exists: " + dto.getEmail());
            }

            if(dto.getName()!=null){
            user.setName(dto.getName());
            }
            user.setEmail(dto.getEmail());
            if(dto.getRole()!=null) {
                user.setRole(dto.getRole());
            }
            if(user.getPassword() != null && !dto.getPassword().isEmpty()){
                user.setPassword(dto.getPassword());
            }

            User updated = userRepository.saveOrUpdate(user, em);
            return toDto(updated);
        });
    }

    public void deleteUser(Long id) {
        TransactionManager.executeInTransactionVoid(em -> {
            if(!userRepository.existsById(id, em)){
                throw new NotFoundException("User not found with id " + id);
            }
            userRepository.deleteById(id, em);
        });
    }

    private UserDto toDto(User saved) {
        UserDto dto = new UserDto();

        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setEmail(saved.getEmail());
        dto.setRole(saved.getRole());

        return dto;
    }



}
