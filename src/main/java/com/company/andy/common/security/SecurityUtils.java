package com.company.andy.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.company.andy.common.utils.Constants.JWT_CLAIM_REALM_ACCESS;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_REALM_ACCESS_ROLES;
import static java.util.stream.Collectors.toSet;

public class SecurityUtils {
    public static Set<String> getJwtRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(JWT_CLAIM_REALM_ACCESS);
        if (realmAccess == null) {
            return Set.of();
        }

        Object roles = realmAccess.get(JWT_CLAIM_REALM_ACCESS_ROLES);

        if (roles == null) {
            return Set.of();
        }

        if (roles instanceof Collection<?> theRoles) {
            return theRoles.stream().map(Object::toString).collect(toSet());
        }
        return Set.of();
    }

    public static String createActorInitiatorFrom(HttpServletRequest request) {
        return "%s[%s]".formatted(request.getMethod(), request.getRequestURI());
    }
}
