package ru.sadyrov.meach.controller;

import lombok.Value;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.sadyrov.meach.domain.Message;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.MessageRepository;
import ru.sadyrov.meach.services.AuthService;
import ru.sadyrov.meach.services.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class MessageController {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final AuthService authService;

    public MessageController(MessageRepository messageRepository, UserService userService, AuthService authService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/getMessages/{receiverLogin}")
    @CrossOrigin(origins = {"http://localhost:8080/", "http://10.17.33.199:8080/"})
    public ResponseEntity<Object> getMessages(@PathVariable String receiverLogin) {
        System.out.println(receiverLogin);
        /*System.out.println(receiverLogin);
        if (userService.getByLogin(receiverLogin).isPresent()) {
            System.out.println(authService.getAuthenticatedUser().getLogin());
            List<Message> messages = messageRepository.findBySenderAndReceiver(
                    authService.getAuthenticatedUser(),
                    userService.getByLogin(receiverLogin).get());
            for (Message m :
                    messages) {
                System.out.println(m.getText());
            }
            return messages;
        } else {
            return null;
        }*/
        JSONArray jsonArray = new JSONArray();
        Set<Message> messages = new HashSet<>();
        messages.addAll(messageRepository.findBySenderAndReceiver(
                authService.getAuthenticatedUser(),
                userService.getByLogin(receiverLogin).get()
        ));
        messages.addAll(messageRepository.findBySenderAndReceiver(
                userService.getByLogin(receiverLogin).get(),
                authService.getAuthenticatedUser()
        ));
        for (Message message : messages) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("senderLogin", message.getSender().getLogin());
            jsonObject.put("receiverLogin", message.getReceiver().getLogin());
            jsonObject.put("text", message.getText());
            jsonObject.put("messageDateTime", message.getMessageDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            jsonArray.put(jsonObject);
        }
        System.out.println(jsonArray);
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

//    @MessageMapping("/addMessage")
//    @SendTo("/topic/messages")
//    public Message addMessage(Message message) {
//        System.out.println("sending message");
//        System.out.println(message.getSender().getLogin());
//        System.out.println(message.getReceiver().getLogin());
//        message.setMessageDateTime(LocalDateTime.now());
//        return messageRepository.save(message);
//    }

    @Transactional
    @MessageMapping("/addMessage")
    @SendTo("/topic/messages")
    public Message addMessage(Map<String, String> message) {
        System.out.println("sending message");
        System.out.println(message);
        Optional<User> sender = userService.getByLogin(message.get("senderLogin"));
        Optional<User> receiver = userService.getByLogin(message.get("receiverLogin"));
        if(sender.isPresent() && receiver.isPresent()){
            Message newMessage = new Message();
            newMessage.setSender(sender.get());
            newMessage.setReceiver(receiver.get());
            newMessage.setText(message.get("text"));
            newMessage.setMessageDateTime(LocalDateTime.now());
            return messageRepository.save(newMessage);
        }
        return null;
    }
}