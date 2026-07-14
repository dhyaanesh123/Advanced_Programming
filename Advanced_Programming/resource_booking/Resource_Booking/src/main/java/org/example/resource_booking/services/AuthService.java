package org.example.resource_booking.services;

import org.example.resource_booking.models.Role;
import org.example.resource_booking.models.User;
import org.example.resource_booking.repositories.UserRepository;

public class AuthService {
    private UserRepository userRepository;
    private User currentUser;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public boolean login(String id, String password) {
        User user = userRepository.findByCredentials(id, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.getRole() == Role.STUDENT;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}