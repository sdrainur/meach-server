package ru.sadyrov.meach.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtRefreshRequest {

    public String refreshToken;

}