package com.avensys.SocialMediaWebApplication.authentication;

import com.avensys.SocialMediaWebApplication.exceptions.DuplicateResourceException;
import com.avensys.SocialMediaWebApplication.exceptions.ResourceNotFoundException;
import com.avensys.SocialMediaWebApplication.jwt.JwtService;
import com.avensys.SocialMediaWebApplication.role.Role;
import com.avensys.SocialMediaWebApplication.role.RoleRepository;
import com.avensys.SocialMediaWebApplication.user.User;
import com.avensys.SocialMediaWebApplication.user.UserRegistrationRequestDTO;
import com.avensys.SocialMediaWebApplication.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User registerUser(UserRegistrationRequestDTO userRegistration) {
        if (userRepository.existsByEmail(userRegistration.email())) {
            throw new DuplicateResourceException("Email already exist!");
        }

        User user = new User();
        user.setEmail(userRegistration.email());
        user.setPassword(passwordEncoder.encode(userRegistration.password()));
        user.setFirstName(userRegistration.firstName());
        user.setLastName(userRegistration.lastName());

        Arrays.stream(userRegistration.roles()).forEach(role -> {
            System.out.println(role);
            Role roleFound = roleRepository.findRolesByName(role);
            user.addRole(roleFound);
        });

        User userSaved = userRepository.save(user);
//        System.out.println(userSaved.getRoles());
        return userSaved;
    }

    public List<String> getUserRoles(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return user.get().getRolesList();
    }

    public AuthenticationResponseDTO getUserAuthResponse(String email, String message) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        String token = jwtService.generateToken(user.get().getEmail());
        return new AuthenticationResponseDTO(
                user.get().getId(),
                message,
                user.get().getEmail(),
                token,
                user.get().getAvatarUrl(),
                user.get().getRolesList());
    }

}
