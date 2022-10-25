package ru.sadyrov.meach.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.UserRepository;
import ru.sadyrov.meach.security.JwtProvider;
import ru.sadyrov.meach.services.AuthService;
import ru.sadyrov.meach.services.UserService;

import java.util.*;

@RestController
@RequestMapping("user")
@CrossOrigin(origins = {"http://localhost:8080", "http://10.17.33.199:8080/"})
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    private final AuthService authService;

    public UserController(UserRepository userRepository, UserService userService, AuthService authService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping()
    public User getUser() {
        return userRepository.findByLogin(authService.getAuthInfo().getUsername());

    }
//    @GetMapping("/{login}")
//    public User getUserByLogin(@PathVariable(value = "login") String login) {
////        return userRepository.findByLogin(authService.getAuthInfo().getUsername());
//        return userRepository.findByLogin(login);
//    }

    @GetMapping("/{login}")
    public Map<String, String> getUserByLogin(@PathVariable(value = "login") String login) {
//        return userRepository.findByLogin(authService.getAuthInfo().getUsername());
        User user = userRepository.findByLogin(login);
        Map<String, String> response = new HashMap<>();
        response.put("firstName", user.getFirstName());
        response.put("secondName", user.getSecondName());
        response.put("login", user.getLogin());
        response.put("city", user.getCity());
        return response;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> allUsers (){
        JSONArray jsonArray = new JSONArray();
        List<User> users = userService.getAll();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("secondName", user.getSecondName());
            jsonObject.put("login", user.getLogin());
            jsonObject.put("city", user.getCity());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }
}
