package ru.sadyrov.meach.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.UserRepository;
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", user.getFirstName());
        jsonObject.put("secondName", user.getSecondName());
        jsonObject.put("login", user.getLogin());
        jsonObject.put("readyToMeet", user.isReadyToMeet());
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> allUsers() {
//        JSONArray jsonArray = new JSONArray();
        List<User> users = userService.getAll();
//        for (User user : users) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("firstName", user.getFirstName());
//            jsonObject.put("secondName", user.getSecondName());
//            jsonObject.put("login", user.getLogin());
//            jsonObject.put("readyToMeet", user.isReadyToMeet());
//            jsonArray.put(jsonObject);
//        }
        JSONArray jsonArray = userService.createUsersJson(users);
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @PostMapping("/send-request/{login}")
    @Transactional
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
//        JSONArray jsonArray = new JSONArray();
//        for (User u : users) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("login", u.getLogin());
//            jsonObject.put("firstName", u.getFirstName());
//            jsonObject.put("secondName", u.getSecondName());
//            jsonArray.put(jsonObject);
//        }
        JSONArray jsonArray = userService.createUsersJson(users);
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @GetMapping("/get-received-requests")
    public ResponseEntity<Object> getReceivedRequests() {
        User user = authService.getAuthenticatedUser();
        Set<User> users = user.getReceivedRequests();
//        JSONArray jsonArray = new JSONArray();
//        for (User u : users) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("login", u.getLogin());
//            jsonObject.put("firstName", u.getFirstName());
//            jsonObject.put("secondName", u.getSecondName());
//            jsonArray.put(jsonObject);
//        }
        JSONArray jsonArray = userService.createUsersJson(users);
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @GetMapping("/get-friends")
    public ResponseEntity<Object> getFriends() {
        User user = authService.getAuthenticatedUser();
        Set<User> friends = user.getFriends();
//        JSONArray jsonArray = new JSONArray();
//        for (User friend : friends) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", friend.getId());
//            jsonObject.put("login", friend.getLogin());
//            jsonObject.put("firstName", friend.getFirstName());
//            jsonObject.put("secondName", friend.getSecondName());
//            jsonArray.put(jsonObject);
//        }
        JSONArray jsonArray = userService.createUsersJson(friends);
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @CrossOrigin(origins = {"http://localhost:8080", "http://10.17.33.199:8080/"})
    @PostMapping("/accept-request/{login}")
    public ResponseEntity<Object> acceptRequest(@PathVariable String login) {
        User authenticatedUser = authService.getAuthenticatedUser();
        if (userService.addAcceptedUser(
                login,
                authenticatedUser.getLogin()
        )) {
//            JSONArray jsonArray = new JSONArray();
//            for (User friend : authenticatedUser.getFriends()) {
//                System.out.println(friend);
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("login", friend.getLogin());
//                jsonObject.put("firstName", friend.getFirstName());
//                jsonObject.put("secondName", friend.getSecondName());
//                jsonArray.put(jsonObject);
//            }
            JSONArray jsonArray = userService.createUsersJson(authenticatedUser.getFriends());
            return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete-friend/{login}")
    public ResponseEntity<Object> deleteFriend(@PathVariable String login) {
        User user = authService.getAuthenticatedUser();
        System.out.println(login);
        if (userService.deleteFriend(user, login)) {
            Set<User> friends = user.getFriends();
//            JSONArray jsonArray = new JSONArray();
//            for (User friend : friends) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("id", friend.getId());
//                jsonObject.put("login", friend.getLogin());
//                jsonObject.put("firstName", friend.getFirstName());
//                jsonObject.put("secondName", friend.getSecondName());
//                jsonArray.put(jsonObject);
//            }
            JSONArray jsonArray = userService.createUsersJson(friends);
            return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/change-status")
    public ResponseEntity<String> changeStatus(@RequestBody Map<String, String> response) {
        System.out.println(response);
        if (userService.changeStatus(
                response.get("login"),
                Boolean.parseBoolean(response.get("status"))
        )) {
            return new ResponseEntity<>("Статус изменен", HttpStatus.OK);
        }
        return new ResponseEntity<>("Статус не изменен", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get-ready-to-meet-users")
    public ResponseEntity<String> getReadyToMeetUsers() {
        JSONArray users = userService.createUsersJson(
                userService.getReadyToMeetUsers()
        );
        return new ResponseEntity<>(users.toString(), HttpStatus.OK);
    }
}
