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
    @CrossOrigin(origins = {"http://localhost:8080/", "http://192.168.137.77:8080"})
    public ResponseEntity<Object> getMessages(@PathVariable String receiverLogin) {
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
            jsonObject.put("id", message.getId());
            jsonObject.put("senderLogin", message.getSender().getLogin());
            jsonObject.put("receiverLogin", message.getReceiver().getLogin());
            jsonObject.put("text", message.getText());
            jsonObject.put("messageDateTime", message.getMessageDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @Transactional
    @MessageMapping("/addMessage")
    @SendTo("/topic/messages")
    public String addMessage(Map<String, String> message) {
        Optional<User> sender = userService.getByLogin(message.get("senderLogin"));
        Optional<User> receiver = userService.getByLogin(message.get("receiverLogin"));
        if(sender.isPresent() && receiver.isPresent()){
            Message newMessage = new Message();
            newMessage.setSender(sender.get());
            newMessage.setReceiver(receiver.get());
            newMessage.setText(message.get("text"));
            newMessage.setMessageDateTime(LocalDateTime.now());
            messageRepository.save(newMessage);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("senderLogin", message.get("senderLogin"));
            jsonObject.put("receiverLogin", message.get("receiverLogin"));
            jsonObject.put("text", newMessage.getText());
            jsonObject.put("messageDateTime", newMessage.getMessageDateTime().format(
                    DateTimeFormatter.ofPattern("HH:mm")
            ));
            return jsonObject.toString();
        }
        return null;
    }
}