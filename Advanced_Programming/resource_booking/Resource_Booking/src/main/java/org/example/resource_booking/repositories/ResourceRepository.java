package org.example.resource_booking.repositories;

import org.example.resource_booking.models.Resource;
import org.example.resource_booking.models.ResourceStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceRepository {
    private static final String RESOURCES_FILE = "data/resources.csv";
    private List<Resource> resources;

    public ResourceRepository() {
        this.resources = new ArrayList<>();
        loadResources();
    }

    public void loadResources() {
        resources.clear();
        File file = new File(RESOURCES_FILE);
        if (!file.exists()) {
            System.out.println("No resources.csv found. It will be created when you save.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    resources.add(new Resource(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            ResourceStatus.valueOf(parts[3].trim().toUpperCase()),
                            parts[4].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading resources: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing ResourceStatus in CSV: " + e.getMessage());
        }
    }


    public List<Resource> getAllResources() {
        return resources;
    }

    public void saveAll() {
        File file = new File(RESOURCES_FILE);
        file.getParentFile().mkdirs(); // Ensure the 'data' folder exists
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Resource resource : resources) {
                writer.println(resource.toCsvLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving resources: " + e.getMessage());
        }
    }

    public Resource findById(String id) {
        for (Resource resource : resources) {
            if (resource.getId().equals(id)) {
                return resource;
            }
        }
        return null;
    }
}
