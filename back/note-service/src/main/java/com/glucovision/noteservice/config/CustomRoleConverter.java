package com.glucovision.noteservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

public class CustomRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String role = jwt.getClaimAsString("role");

        System.out.println("🔐 JWT ROLE trouvé : " + role);

        if (role == null || role.isBlank()) {
            return List.of();
        }

        return List.of(new SimpleGrantedAuthority("ROLE_"+role));

    }
}