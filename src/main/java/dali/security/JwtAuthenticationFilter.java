package dali.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        // Start with authorities from DB (if any)
                        Collection<? extends GrantedAuthority> dbAuths = userDetails.getAuthorities();
                        List<GrantedAuthority> merged = new ArrayList<>(dbAuths);

                        // 🔐 Add authority based on JWT claim(s)
                        String roleClaim = jwtUtil.extractRole(jwt); // e.g. "ROLE_ADMIN" or "ROLE_USER"
                        if (roleClaim != null && !roleClaim.isBlank()) {
                            // Spring's hasRole("ADMIN") expects authorities like "ROLE_ADMIN"
                            String normalized = roleClaim.startsWith("ROLE_") ? roleClaim : "ROLE_" + roleClaim;
                            // Avoid duplicates
                            boolean exists = merged.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(normalized));
                            if (!exists) merged.add(new SimpleGrantedAuthority(normalized));
                        }

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, merged);

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception ex) {
                // Token error => do not authenticate; let the chain continue.
                // Optionally log at DEBUG level.
            }
        }

        filterChain.doFilter(request, response);
    }
}
