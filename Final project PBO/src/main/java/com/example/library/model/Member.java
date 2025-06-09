package com.example.library.model;

import javafx.beans.property.SimpleStringProperty;

public class Member {
    private final SimpleStringProperty memberId;
    private final SimpleStringProperty name;
    private final SimpleStringProperty major;
    private final SimpleStringProperty email;
    private String password; // Password untuk login member

    public Member(String memberId, String name, String major, String email, String password) {
        this.memberId = new SimpleStringProperty(memberId);
        this.name = new SimpleStringProperty(name);
        this.major = new SimpleStringProperty(major);
        this.email = new SimpleStringProperty(email);
        this.password = password;
    }

    // Getter dan Setter
    public String getMemberId() { return memberId.get(); }
    public void setMemberId(String memberId) { this.memberId.set(memberId); }
    public SimpleStringProperty memberIdProperty() { return memberId; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public SimpleStringProperty nameProperty() { return name; }

    public String getMajor() { return major.get(); }
    public void setMajor(String major) { this.major.set(major); }
    public SimpleStringProperty majorProperty() { return major; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public SimpleStringProperty emailProperty() { return email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}