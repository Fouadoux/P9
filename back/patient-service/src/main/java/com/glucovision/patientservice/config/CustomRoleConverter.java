package com.glucovision.patientservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts a JWT into a collection of {@link GrantedAuthority} instances based on its "roles" claim.
 * <p>
 * Each role is mapped to a {@code SimpleGrantedAuthority} with a "ROLE_" prefix, as expected by Spring Security.
 * Returns an empty collection if no roles are found.
 */
@Slf4j
public class CustomRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Converts the "roles" claim from the JWT into a collection of granted authorities.
     *
     * @param jwt the JWT containing user roles
     * @return a collection of granted authorities derived from the roles
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");

        if (roles == null || roles.isEmpty()) {
            log.info("[ROLE CONVERTER] No roles found in token for subject: {}", jwt.getSubject());
            return List.of();
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        log.info("[ROLE CONVERTER] Roles extracted for subject {}: {}", jwt.getSubject(), roles);
        return authorities;
    }
}
