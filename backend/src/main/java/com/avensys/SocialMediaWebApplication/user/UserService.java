package com.avensys.SocialMediaWebApplication.user;

import com.avensys.SocialMediaWebApplication.exceptions.ResourceAccessDeniedException;
import com.avensys.SocialMediaWebApplication.exceptions.ResourceNotFoundException;
import com.avensys.SocialMediaWebApplication.jwt.JwtService;
import com.avensys.SocialMediaWebApplication.role.Role;
import com.avensys.SocialMediaWebApplication.role.RoleRepository;
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

    public UserUpdateResponseDTO updateUserById(long id, UserUpdateRequestDTO userUpdateRequest) {
        User userUpdate = findUserById(id);

        userUpdate.setPassword(passwordEncoder.encode(userUpdateRequest.password()));
        userUpdate.setEmail(userUpdateRequest.email());
        userUpdate.setFirstName(userUpdateRequest.firstName());
        userUpdate.setLastName(userUpdateRequest.lastName());

        User updatedUser = userRepository.save(userUpdate);
        String token = jwtService.generateToken(updatedUser.getEmail());

        return userToUserUpdateResponseDTO(userUpdate, token);
    }

    public UserUpdateResponseDTO updateUserByIdWithRoles(long id, UserUpdateRequestDTO userUpdateRequest) {
        User userUpdate = findUserById(id);

        // Check if user is admin or user to update belong to user before user is allowed update user profile
        if (!checkIsAdmin()){
            throw new ResourceAccessDeniedException("Access denied to resource");
        }

        userUpdate.setPassword(passwordEncoder.encode(userUpdateRequest.password()));
        userUpdate.setEmail(userUpdateRequest.email());
        userUpdate.setFirstName(userUpdateRequest.firstName());
        userUpdate.setLastName(userUpdateRequest.lastName());

        // Update Roles
        userUpdate.getRoles().clear();
        Arrays.stream(userUpdateRequest.roles()).forEach(role -> {
            System.out.println(role);
            Role roleFound = roleRepository.findRolesByName(role);
            userUpdate.addRole(roleFound);
        });

        User updatedUser = userRepository.save(userUpdate);
        String token = jwtService.generateToken(updatedUser.getEmail());

        return userToUserUpdateResponseDTO(userUpdate, token);
    }

    public void deleteUserById(long id) {
        User user = findUserById(id);
        userRepository.delete(user);
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

    private UserUpdateResponseDTO userToUserUpdateResponseDTO(User user, String token) {
        return new UserUpdateResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatarUrl(),
                token,
                user.getRolesList(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private void checkUserToUpdateBelongsToUser(User UserUpdateRequest) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findByEmail(principal.getName());
        if (UserUpdateRequest.getId() != user.get().getId()) {
            throw new ResourceAccessDeniedException("Access denied to resource");
        }
    }

    private boolean checkIsAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }
}
