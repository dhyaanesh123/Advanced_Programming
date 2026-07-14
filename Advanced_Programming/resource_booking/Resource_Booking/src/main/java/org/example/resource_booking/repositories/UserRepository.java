package org.example.resource_booking.repositories;

import org.example.resource_booking.models.Role;
import org.example.resource_booking.models.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String USERS_FILE = "data/users.csv";
    private static final String PROFILES_FILE = "data/student_profiles.csv";
    private List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
        loadUsers();
        createDefaultAdminIfNeeded();
    }

    private void createDefaultAdminIfNeeded() {
        if (users.isEmpty()) {
            User admin = new User("admin001", "admin123", Role.ADMIN);
            admin.setFullName("System Administrator");
            admin.setEmail("admin@campus.edu");
            admin.setProgramme("N/A");
            users.add(admin);
            saveAll();
        }
    }

    public void loadUsers() {
        users.clear();
        loadAuthFile();
        loadProfiles();
    }

    private void loadAuthFile() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("Users file not found, will create default admin.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0].trim(), parts[1].trim(),
                            Role.valueOf(parts[2].trim())));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void loadProfiles() {
        File file = new File(PROFILES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String id = parts[0].trim();
                    for (User user : users) {
                        if (user.getId().equals(id)) {
                            user.setFullName(parts[1].trim());
                            user.setEmail(parts[2].trim());
                            user.setProgramme(parts[3].trim());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading profiles: " + e.getMessage());
        }
    }

    public void saveAll() {
        saveAuthFile();
        saveProfileFile();
    }

    private void saveAuthFile() {
        File file = new File(USERS_FILE);
        file.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (User user : users) {
                writer.println(user.toAuthCsvLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void saveProfileFile() {
        File file = new File(PROFILES_FILE);
        file.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (User user : users) {
                if (user.getRole() == Role.STUDENT && !user.getFullName().isEmpty()) {
                    writer.println(user.toProfileCsvLine());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving profiles: " + e.getMessage());
        }
    }

    public User findByCredentials(String id, String password) {
        for (User user : users) {
            if (user.getId().equals(id) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public boolean existsById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addUser(User user) {
        users.add(user);
        saveAll();
    }

    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == Role.STUDENT) {
                students.add(user);
            }
        }
        return students;
    }

    public User findById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                saveAll();
                return;
            }
        }
    }
}