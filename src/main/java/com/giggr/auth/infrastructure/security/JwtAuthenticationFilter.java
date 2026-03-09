package com.giggr.auth.infrastructure.security;


import com.giggr.auth.application.JwtService;
import com.giggr.identity.application.IdentityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final IdentityService identityService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        if (token != null && jwtService.isValid(token)) {

            String digitalId = jwtService.extractDigitalId(token);

            try {
                identityService.ensureActiveByDigitalId(digitalId);
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            digitalId,
                            null,
                            Collections.emptyList()
                    );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("SESSION".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}