package com.example.frontendfinalproject.classes;

public class Appointment {

    private String id;
    private String date;
    private String time;
    private String status;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Default constructor required for Firebase
    public Appointment(String userId) {
        this.userId = userId;
    }
    public Appointment() {
    }

    public Appointment(String id, String date, String time, String status, String userId) {

        this.id = id;
        this.date = date;
        this.time = time;
        this.status = status;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
