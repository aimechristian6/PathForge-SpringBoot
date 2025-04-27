package com.example.pathforge.service;

import com.example.pathforge.listener.CustomSessionListener;
import com.example.pathforge.model.User;
import com.example.pathforge.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomSessionListener sessionListener;

    // Fetch user by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Fetch user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Add user to active sessions
    public void addUserToSession(HttpSession session, User user) {
        sessionListener.addUserToSession(session, user);
    }

    // Get active admins count
    public int getActiveAdminsCount() {
        return sessionListener.getActiveAdminsCount();
    }

    // Get list of active users
    public ConcurrentHashMap<String, User> getActiveUsers() {
        return sessionListener.getActiveUsers();
    }
}