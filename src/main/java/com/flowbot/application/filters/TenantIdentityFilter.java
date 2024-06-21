package com.flowbot.application.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static com.flowbot.application.shared.AuthUtils.identifyResourceOwner;
import static com.flowbot.application.shared.AuthUtils.setTenant;

@ConditionalOnExpression("${keycloak.enabled:true} == true")
@Component
public class TenantIdentityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // no momento auth com websocket eh feito de outra forma
        if (request.getRequestURI().equals("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        identifyRequest(authentication);

        filterChain.doFilter(request, response);
    }

    private void identifyRequest(Authentication authentication) {
        if (Objects.isNull(authentication))
            return;

        Object principal = authentication.getPrincipal();
        var resourceOwner = identifyResourceOwner(principal);
        logger.info(String.format("Identified resource owner: %s", resourceOwner));
        setTenant(resourceOwner);
    }
}
