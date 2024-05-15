package com.flowbot.application.filters;

import com.flowbot.application.context.TenantThreads;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ConditionalOnExpression("${keycloak.enabled:true} == true")
@Component
public class TenantIdentityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        identifyRequest(authentication);

        filterChain.doFilter(request, response);
    }

    private void identifyRequest(Authentication authentication) {
        if (Objects.isNull(authentication))
            return;

        Object principal = authentication.getPrincipal();
        String resourceOwner = extractResourceOwner((Jwt) principal);
        logger.info(String.format("Identified resource owner: %s", resourceOwner));

        TenantThreads.setTenantId(resourceOwner);
    }

    private String extractResourceOwner(final Jwt jwt) {
        var keysSearcheds = List.of("sub");
        var resourceOwner = new ArrayList<>();
        for (String key : keysSearcheds) {
            if (jwt.getClaims().containsKey(key)) {
                resourceOwner.add(jwt.getClaims().get(key).toString() + "-");
            }
        }

        var joined = Strings.join(resourceOwner, '-');
        return joined
                .replace("-", "")
                .replace(".", "");
    }
}
