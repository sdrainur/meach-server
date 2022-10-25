package ru.sadyrov.meach.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.UserRepository;
import ru.sadyrov.meach.services.UserService;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://10.17.33.199:8080/"})
public class SignUpController {
    final private UserRepository userRepository;
    final private UserService userService;

    public SignUpController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/signup")
    ResponseEntity<String> signUp(@RequestBody User user) {
        System.out.println("signing up");
        if(userService.addUser(user))
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/signup/activate")
    ResponseEntity<String> activate(@RequestBody String activationCode) {
        JSONObject jo = new JSONObject(activationCode);
        boolean isActivated = userService.activateUser(jo.getString("activationCode"));
        if (!isActivated) {
            return new ResponseEntity<>("Wrong activation code", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}

