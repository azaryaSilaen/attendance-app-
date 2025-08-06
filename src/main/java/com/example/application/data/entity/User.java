package com.example.application.data.entity;

import com.example.application.data.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {


    @Column(name = "username")
    private String username;
    @JsonIgnore
    @Column(name = "hashed_password")
    private String hashedPassword;
    @Column(name = "club_name")
    private String clubName;
    //@ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private Role roles;

    public User(String username, String hashedPassword, String roles, String clubName) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.roles = Role.valueOf(roles);
        this.clubName = clubName;
    }

    public User() {

    }


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Role getRoles() {
        return roles;
    }
    public void setRoles(Role roles) {
        this.roles = roles;
    }

}
