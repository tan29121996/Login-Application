package com.avensys.SocialMediaWebApplication.user;

import com.avensys.SocialMediaWebApplication.jwt.JwtService;
import com.avensys.SocialMediaWebApplication.role.Role;
import com.avensys.SocialMediaWebApplication.role.RoleRepository;
import com.avensys.SocialMediaWebApplication.exceptions.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResourceNotFoundException("User with id %s not found".formatted(id));
        }
    }

    public UserResponseDTO findUserByIdDTO(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userToUserResponseDTO(user.get());
        } else {
            throw new ResourceNotFoundException("User with id %s not found".formatted(id));
        }
    }

    public boolean existUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserResponseDTO> searchUser(String keyword) {
        List<User> users = userRepository.findByUserByFirstNameOrLastNameOrEmail(keyword);
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(user -> userToUserResponseDTO(user))
                .collect(Collectors.toList());
//        users.stream().map(this::userToUserResponseDTO).toList();
        return userResponseDTOs;
    }


    private UserResponseDTO userToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatarUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private boolean checkIsAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }

}
