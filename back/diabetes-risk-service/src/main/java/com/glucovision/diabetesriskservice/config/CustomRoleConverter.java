package com.glucovision.diabetesriskservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

/**
 * Custom converter that extracts the user's role from a JWT and converts it
 * into a Spring Security {@link GrantedAuthority}.
 * <p>
 * This implementation assumes that the JWT contains a single string claim named "roles"
 * (e.g., "USER", "ADMIN") and maps it to a {@code ROLE_} prefixed authority.
 * </p>
 */
@Slf4j
public class CustomRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Converts the "roles" claim from the JWT into a list of {@link GrantedAuthority}.
     *
     * @param jwt The JWT containing the user's claims.
     * @return A collection of granted authorities extracted from the token.
     *         If the "roles" claim is missing or blank, an empty list is returned.
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String role = jwt.getClaimAsString("roles");

        if (role == null || role.isBlank()) {
            log.warn("No role found in JWT token.");
            return List.of();
        }

        String authority = "ROLE_" + role;
        log.debug("Extracted role from JWT: {}", authority);

        return List.of(new SimpleGrantedAuthority(authority));
    }
}
