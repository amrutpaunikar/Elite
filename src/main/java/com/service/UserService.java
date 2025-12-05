package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.model.User;
import com.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User createUser(String email, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        User u = new User(email, hashed);
        return userRepository.save(u);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(User user, String rawPassword){
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public Optional<User> findById(String id){
        return userRepository.findById(id);
    }

    public User save(User u){
        return userRepository.save(u);
    }
}
