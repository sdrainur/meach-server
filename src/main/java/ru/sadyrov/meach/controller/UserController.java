package ru.sadyrov.meach.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
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

//    @GetMapping()
//    public ResponseEntity<Object> getUser() {
//        User user = userRepository.findByLogin(authService.getAuthInfo().getUsername());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("firstName", user.getFirstName());
//        jsonObject.put("secondName", user.getSecondName());
//        jsonObject.put("login", user.getLogin());
//        jsonObject.put("city", user.getCity());
//        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
//    }
//    @GetMapping("/{login}")
//    public User getUserByLogin(@PathVariable(value = "login") String login) {
////        return userRepository.findByLogin(authService.getAuthInfo().getUsername());
//        return userRepository.findByLogin(login);
//    }

    @GetMapping("/{login}")
    public ResponseEntity<Object> getUserByLogin(@PathVariable(value = "login") String login) {
//        return userRepository.findByLogin(authService.getAuthInfo().getUsername());
        User user = null;
        if (login != null)
            user = userRepository.findByLogin(login);
        else
            user = userRepository.findByLogin(authService.getAuthInfo().getUsername());
        //        Map<String, String> response = new HashMap<>();
//        response.put("firstName", user.getFirstName());
//        response.put("secondName", user.getSecondName());
//        response.put("login", user.getLogin());
//        response.put("city", user.getCity());
//        System.out.println(response);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", user.getFirstName());
        jsonObject.put("secondName", user.getSecondName());
        jsonObject.put("login", user.getLogin());
        jsonObject.put("city", user.getCity());
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> allUsers() {
        JSONArray jsonArray = new JSONArray();
        List<User> users = userService.getAll();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("secondName", user.getSecondName());
            jsonObject.put("login", user.getLogin());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @PostMapping("/send-request/{login}")
    public ResponseEntity<Object> sendRequest(@PathVariable String login) {
        User authUser = authService.getAuthenticatedUser();
        if (userService.sendRequest(authUser.getLogin(), login))
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get-sent-requests")
    public ResponseEntity<Object> getSentRequests() {
        User user = authService.getAuthenticatedUser();
        Set<User> users = user.getSentRequests();
        JSONArray jsonArray = new JSONArray();
        for (User u : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("login", u.getLogin());
            jsonObject.put("firstName", u.getFirstName());
            jsonObject.put("secondName", u.getSecondName());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @GetMapping("/get-received-requests")
    public ResponseEntity<Object> getReceivedRequests() {
        User user = authService.getAuthenticatedUser();
        Set<User> users = user.getReceivedRequests();
        JSONArray jsonArray = new JSONArray();
        for (User u : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("login", u.getLogin());
            jsonObject.put("firstName", u.getFirstName());
            jsonObject.put("secondName", u.getSecondName());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @GetMapping("/get-friends")
    public ResponseEntity<Object> getFriends(){
        System.out.println();
        User user = authService.getAuthenticatedUser();
        Set<User> friends = user.getFriends();
        JSONArray jsonArray = new JSONArray();
        for (User friend : friends) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("login", friend.getLogin());
            jsonObject.put("firstName", friend.getFirstName());
            jsonObject.put("secondName", friend.getSecondName());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

//    @CrossOrigin(origins = {"http://localhost:8080", "http://10.17.33.199:8080/"})
    @PostMapping("/accept-request/{login}")
    public ResponseEntity<Object> acceptRequest(@PathVariable String login) {
        User authenticatedUser = authService.getAuthenticatedUser();
        if (userService.addAcceptedUser(
                login,
                authenticatedUser.getLogin()
        )) {
            JSONArray jsonArray = new JSONArray();
            for (User friend : authenticatedUser.getFriends()) {
                System.out.println(friend);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("login", friend.getLogin());
                jsonObject.put("firstName", friend.getFirstName());
                jsonObject.put("secondName", friend.getSecondName());
                jsonArray.put(jsonObject);
            }
            return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
