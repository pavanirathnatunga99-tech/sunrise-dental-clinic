package com.sunrise.dental.security;

import com.sunrise.dental.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserRepository users;

    public JwtFilter(JwtService jwt, UserRepository users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res,
            @NonNull FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwt.username(h.substring(7));
                users.findByUsername(username).filter(u -> u.isEnabled())
                        .ifPresent(u -> SecurityContextHolder.getContext()
                                .setAuthentication(new UsernamePasswordAuthenticationToken(username, null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())))));
            } catch (Exception ignored) {
            }
        }
        chain.doFilter(req, res);
    }
}
