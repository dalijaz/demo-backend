// src/main/java/dali/security/JwtAuthenticationFilter.java
package dali.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
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
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain) throws ServletException, IOException {

    final String path = request.getRequestURI();
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())
        || path.startsWith("/auth/")
        || path.startsWith("/verify/")) {
      chain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);
    try {
      String username = jwtUtil.extractUsername(jwt);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtUtil.validateToken(jwt, userDetails)) {
          Collection<? extends GrantedAuthority> db = userDetails.getAuthorities();
          List<GrantedAuthority> merged = new ArrayList<>(db);
          String roleClaim = jwtUtil.extractRole(jwt);
          if (roleClaim != null && !roleClaim.isBlank()) {
            String norm = roleClaim.startsWith("ROLE_") ? roleClaim : "ROLE_" + roleClaim;
            boolean exists = merged.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(norm));
            if (!exists) merged.add(new SimpleGrantedAuthority(norm));
          }
          var auth = new UsernamePasswordAuthenticationToken(userDetails, null, merged);
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    } catch (Exception ignore) { }
    chain.doFilter(request, response);
  }
}
