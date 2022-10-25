package ru.sadyrov.meach.controller;

import lombok.Value;
import org.json.JSONObject;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Set<Message> getMessages(@PathVariable String receiverLogin) {
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
        Set<Message> messages = new HashSet<>();
        messages.addAll(messageRepository.findBySenderAndReceiver(
                authService.getAuthenticatedUser(),
                userService.getByLogin(receiverLogin).get()
        ));
        messages.addAll(messageRepository.findBySenderAndReceiver(
                userService.getByLogin(receiverLogin).get(),
                authService.getAuthenticatedUser()
        ));
        return messages;
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
        Message newMessage = new Message();
        newMessage.setSender(userService.getByLogin(message.get("senderLogin")).get());
        System.out.println(newMessage.getSender().getId());
        newMessage.setReceiver(userService.getByLogin(message.get("receiverLogin")).get());
        System.out.println(newMessage.getReceiver().getId());
        newMessage.setText(message.get("text"));
        newMessage.setMessageDateTime(LocalDateTime.now());
        return messageRepository.save(newMessage);
    }
}