package com.avensys.SocialMediaWebApplication.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/email")
    public ResponseEntity<Boolean> existUserByEmail(@RequestParam String email) {
        Boolean isEmailExist = userService.existUserByEmail(email);
        return new ResponseEntity<>(isEmailExist, HttpStatus.OK);
    }


    @GetMapping("users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable long userId) {
        UserResponseDTO user = userService.findUserByIdDTO(userId);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
