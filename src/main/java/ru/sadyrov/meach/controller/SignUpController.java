package ru.sadyrov.meach.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.UserRepository;
import ru.sadyrov.meach.services.UserService;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://192.168.137.77:8080"})
public class SignUpController {
    final private UserRepository userRepository;
    final private UserService userService;

    public SignUpController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody User user) {
        System.out.println("signing up");
        System.out.println(user.isReadyToMeet());
        Optional<User> optionalUserByMail = userService.getByMail(user.getEmail());
        Optional<User> optionalUserByLogin = userService.getByLogin(user.getLogin());
        if (optionalUserByMail.isPresent()) {
            return new ResponseEntity<>("Данная почта уже зарегистрирована", HttpStatus.BAD_REQUEST);
        } else if (optionalUserByLogin.isPresent()) {
            return new ResponseEntity<>("Данный логин уже зарегистрирован", HttpStatus.BAD_REQUEST);
        } else {
            userService.addUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/signup/activate")
    ResponseEntity<String> activate(@RequestBody Map<String, String> request) {
        System.out.println(request);
        String activationCode = request.get("activationCode");
        System.out.println(activationCode);
        boolean isActivated = userService.activateUser(activationCode);
        if (!isActivated) {
            return new ResponseEntity<>("Wrong activation code", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/signup/reset-password")
    ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        System.out.println(request);
        Optional<User> userOptional = userService.getByMail(request.get("mail"));
        if (userOptional.isPresent()) {
            userService.resetPassword(userOptional.get());
            return new ResponseEntity<>("Email was send", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/signup/set-password")
    ResponseEntity<String> setPassword(@RequestBody Map<String, String> request) {
        System.out.println(request);
        if (userService.setPassword(
                request.get("activationCode"),
                request.get("password")
        )) {
            return new ResponseEntity<>("Password is updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Code is wrong", HttpStatus.BAD_REQUEST);
        }
    }
}

