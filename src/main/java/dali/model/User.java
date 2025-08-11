// src/main/java/dali/model/User.java
package dali.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Account activation flag
    @Column(nullable = false)
    private boolean enabled = false;

    // Email verification token
    @Column(name = "verification_token")
    private String verificationToken;

    // 🔹 Persisted role (maps to your MySQL ENUM('ROLE_ADMIN','ROLE_USER'))
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.enabled = false;
        this.role = Role.ROLE_USER;
    }

    public boolean isVerified() { return this.enabled; }

    // Getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
