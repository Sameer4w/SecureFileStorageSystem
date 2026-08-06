package com.example.securefilestoragesystem.controller;
import com.example.securefilestoragesystem.service.EmailService;
import org.springframework.http.ResponseEntity;
import com.example.securefilestoragesystem.entity.User;
import com.example.securefilestoragesystem.service.UserService;
import com.example.securefilestoragesystem.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

        User savedUser = userService.registerUser(user);

        emailService.sendEmail(

                savedUser.getEmail(),

                "Welcome to Secure File Storage",

                "Hello " + savedUser.getFullName() +

                        "\n\nYour account has been created successfully."

                        + "\n\nThank you for using Secure File Storage."

        );

        return savedUser;

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user,
                                            HttpSession session) {

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        boolean isValid =
                userService.loginUser(user.getEmail(), user.getPassword());

        if (!isValid) {
            return ResponseEntity.status(401)
                    .body("Invalid Email or Password");
        }

        session.setAttribute("userEmail", user.getEmail());

        User loggedUser = userService.findByEmail(user.getEmail());

        session.setAttribute("userName", loggedUser.getFullName());

        System.out.println("Login Success : " + user.getEmail());

        return ResponseEntity.ok("Login Successful");
    }

    @GetMapping("/profile")
    public User getProfile(HttpSession session) {

        String userEmail = (String) session.getAttribute("userEmail");

        Optional<User> user = userRepository.findByEmail(userEmail);

        return user.orElse(null);
    }
}