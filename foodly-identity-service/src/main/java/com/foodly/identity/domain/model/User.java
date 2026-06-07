package com.foodly.identity.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_email",    columnNames = "email"),
        @UniqueConstraint(name = "uq_users_username", columnNames = "username")
    },
    indexes = {
        @Index(name = "idx_users_email",    columnList = "email"),
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_active",   columnList = "active")
    }
)
public class User {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @NotBlank(message = "La contraseña hasheada es obligatoria")
    @Column(name = "password_hash", length = 60, nullable = false)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    protected User() {}

    public User(String email,
                String username,
                String passwordHash,
                String firstName,
                String lastName,
                Set<UserRole> roles) {
        this.email        = email;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.roles        = (roles != null) ? roles : new HashSet<>();
    }

    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    public void deactivate() {
        this.active = false;
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public String getId()               { return id; }
    public String getEmail()            { return email; }
    public String getUsername()         { return username; }
    public String getPasswordHash()     { return passwordHash; }
    public String getFirstName()        { return firstName; }
    public String getLastName()         { return lastName; }
    public String getPhoneNumber()      { return phoneNumber; }
    public Set<UserRole> getRoles()     { return roles; }
    public boolean isActive()           { return active; }
    public boolean isEmailVerified()    { return emailVerified; }
    public Instant getCreatedAt()       { return createdAt; }
    public Instant getUpdatedAt()       { return updatedAt; }

    public void setEmail(String email)             { this.email = email; }
    public void setUsername(String username)       { this.username = username; }
    public void setPasswordHash(String hash)       { this.passwordHash = hash; }
    public void setFirstName(String firstName)     { this.firstName = firstName; }
    public void setLastName(String lastName)       { this.lastName = lastName; }
    public void setPhoneNumber(String phone)       { this.phoneNumber = phone; }
    public void setRoles(Set<UserRole> roles)      { this.roles = roles; }
    public void setActive(boolean active)          { this.active = active; }
    public void setEmailVerified(boolean verified) { this.emailVerified = verified; }

    @Override
    public String toString() {
        return "User{id='" + id + "', email='" + email + "', username='" + username +
               "', roles=" + roles + ", active=" + active + "}";
    }
}
