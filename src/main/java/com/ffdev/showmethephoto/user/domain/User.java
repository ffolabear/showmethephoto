package com.ffdev.showmethephoto.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Getter
    @Column(name = "id", nullable = false)
    private UUID id;

    @Getter
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Getter
    @Column(name = "password_hash", nullable = false, unique = true, length = 255)
    private String passwordHash;

    @Getter
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status;

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    private User(String email, String passwordHash, String name) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
    }

    public static User create(String email, String passwordHash, String name) {
        return new User(email, passwordHash, name);
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

}
