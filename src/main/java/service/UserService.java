package com.example.securefilestoragesystem.service;

import com.example.securefilestoragesystem.entity.User;
import com.example.securefilestoragesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    // Register User
    public User registerUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Login User
    public boolean loginUser(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            return passwordEncoder.matches(password, user.getPassword());
        }

        return false;
    }

    // Find User by Email
    public User findByEmail(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        return optionalUser.orElse(null);
    }
}