// src/main/java/dali/service/CustomUserDetailsService.java
package dali.service;

import dali.model.User;
import dali.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // ✅ Use role from DB (ROLE_USER / ROLE_ADMIN)
        var authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .accountLocked(false)
            .disabled(!user.isEnabled())
            .build();
    }
}
