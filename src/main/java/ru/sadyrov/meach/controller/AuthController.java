package ru.sadyrov.meach.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sadyrov.meach.security.JwtRequest;
import ru.sadyrov.meach.security.JwtResponse;
import ru.sadyrov.meach.security.JwtRefreshRequest;
import ru.sadyrov.meach.services.AuthService;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8080", "http://192.168.137.77:8080", "*"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody JwtRequest authRequest) throws AuthException {
        final JwtResponse token = authService.login(authRequest);
        if(token!=null)
            return new ResponseEntity<>(token, HttpStatus.OK);
        else
            return new ResponseEntity<>("Неверный логин или пароль", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getAccessToken(@RequestBody JwtRefreshRequest request) throws AuthException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody JwtRefreshRequest request) throws AuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}