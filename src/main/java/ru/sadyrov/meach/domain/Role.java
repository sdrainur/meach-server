package ru.sadyrov.meach.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER"),
    COACH("COACH"),
    MENTOR("MENTOR");

    private final String vale;

    @Override
    public String getAuthority() {
        return vale;
    }

}