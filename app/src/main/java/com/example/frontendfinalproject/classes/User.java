package com.example.frontendfinalproject.classes;

public class User {
    private String name;
    private String email;
    private String phone;
    private String id;

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }

    public User(String name, String email,String phone,String id) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
