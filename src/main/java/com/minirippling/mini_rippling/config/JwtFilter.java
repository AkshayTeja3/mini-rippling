package com.minirippling.mini_rippling.config;

import com.minirippling.mini_rippling.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // STEP 1 — get Authorization header
        String authHeader = request.getHeader("Authorization");

        // STEP 2 — check if header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // STEP 3 — extract token
        String token = authHeader.substring(7); // remove "Bearer " prefix

        // STEP 4 — validate token
        if (jwtService.isTokenValid(token)) {
            // STEP 5 — set authentication in Spring Security context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            jwtService.extractEmployeeId(token),
                            null,
                            new ArrayList<>()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // STEP 6 — continue with the request
        filterChain.doFilter(request, response);
    }
}