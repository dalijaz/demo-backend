// src/main/java/dali/controller/admin/AdminUserController.java
package dali.controller.admin;

import dali.model.Role;
import dali.model.User;
import dali.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {

    private final UserRepository users;

    public AdminUserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() {
        return users.findAll();
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public User enable(@PathVariable Long id) {
        var u = users.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setEnabled(true);
        u.setVerificationToken(null);
        return users.save(u);
    }

    @PutMapping("/{id}/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public User setRole(@PathVariable Long id, @PathVariable String role) {
        var u = users.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Accept "ADMIN" / "USER" or "ROLE_ADMIN" / "ROLE_USER"
        String normalized = role.toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        Role enumRole = Role.valueOf(normalized); // must be ROLE_ADMIN or ROLE_USER
        u.setRole(enumRole);
        return users.save(u);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        users.deleteById(id);
    }
}
