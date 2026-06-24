package com.github.andrz25.delta.user_service.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", initialValue = 1, allocationSize = 50)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email_address", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    protected User() {
    }

    private User(String fullName, String username, String email, LocalDate dateOfBirth,
            Role role) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    public static class Builder {
        private String fullName;
        private String username;
        private String email;
        private LocalDate dateOfBirth;
        private Role role;

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public User build() {
            return new User(fullName, username, email, dateOfBirth, role);
        }
    }
}
