package com.example.pathforge.repository;

import com.example.pathforge.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to map database rows to User objects
    private final RowMapper<User> userRowMapper = (ResultSet rs, int rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setProfilePicture(rs.getBytes("profile_picture"));
        user.setRole(rs.getString("role"));
        user.setPassword(rs.getString("password"));
        return user;
    };

    // Save a new user (signup)
    public void save(User user) {
        String sql = "INSERT INTO users (email, username, profile_picture, role, password) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getUsername(),
                user.getProfilePicture(),
                user.getRole(),
                user.getPassword());
    }

    public void update(User user) {
        String sql = "UPDATE users SET email = ?, username = ?, profile_picture = ?, role = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getUsername(),
                user.getProfilePicture(),
                user.getRole(),
                user.getPassword(),
                user.getId());
    }

    // Find a user by email (case-insensitive)
    public User findByEmail(String email) {
        System.out.println("Checking users table for email: " + email);
        String sql = "SELECT * FROM users WHERE UPPER(email) = UPPER(?)";
        User user = jdbcTemplate.query(sql, new Object[]{email}, rs -> {
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setUsername(rs.getString("username"));
                u.setProfilePicture(rs.getBytes("profile_picture"));
                u.setRole(rs.getString("role"));
                u.setPassword(rs.getString("password"));
                System.out.println("User found in users table: " + email);
                return u;
            }
            System.out.println("User not found in users table: " + email);
            return null;
        });
        return user;
    }

    // Find a user by username
    public User findByUsername(String username) {
        System.out.println("Checking users table for username: " + username);
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = jdbcTemplate.query(sql, new Object[]{username}, rs -> {
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setUsername(rs.getString("username"));
                u.setProfilePicture(rs.getBytes("profile_picture"));
                u.setRole(rs.getString("role"));
                u.setPassword(rs.getString("password"));
                System.out.println("User found in users table: " + username);
                return u;
            }
            System.out.println("User not found in users table: " + username);
            return null;
        });
        return user;
    }
}