package com.example.demo.model;
import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class UserModel {
    @Id
    private Long id;
    private String employeeType;
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }
}