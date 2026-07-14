package org.example.resource_booking.models;

public class Resource {
    private String id;
    private String name;
    private String location;
    private ResourceStatus status;
    private String type;

    public Resource(String id, String name, String location, ResourceStatus status, String type) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = status;
        this.type = type;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getLocation(){
        return location;
    }
    public ResourceStatus getStatus(){
        return status;
    }
    public String getType(){
        return type;
    }

    public void setStatus(ResourceStatus status){
        this.status = status;
    }
    public String toCsvLine(){
        return id + "," + name + "," + location + "," + status.name() + "," + type;
    }
}
