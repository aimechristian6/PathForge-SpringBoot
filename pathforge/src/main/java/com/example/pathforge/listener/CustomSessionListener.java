package com.example.pathforge.listener;

import com.example.pathforge.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomSessionListener implements HttpSessionListener {
    // Store active sessions with username as the key and user object as the value
    private static final ConcurrentHashMap<String, User> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        System.out.println("Session Created: " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String username = (String) session.getAttribute("username");
        if (username != null) {
            activeSessions.remove(username);
        }
        System.out.println("Session Destroyed: " + session.getId());
    }

    // Method to add a user to active sessions when they log in
    public void addUserToSession(HttpSession session, User user) {
        activeSessions.put(user.getUsername(), user);
        session.setAttribute("username", user.getUsername());
    }

    // Method to get the count of active admins
    public int getActiveAdminsCount() {
        return (int) activeSessions.values().stream()
                .filter(user -> "ADMIN".equalsIgnoreCase(user.getRole()))
                .count();
    }

    // Method to get the list of active users
    public ConcurrentHashMap<String, User> getActiveUsers() {
        return activeSessions;
    }
}