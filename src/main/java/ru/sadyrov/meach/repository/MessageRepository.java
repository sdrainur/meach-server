package ru.sadyrov.meach.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import ru.sadyrov.meach.domain.Message;
import ru.sadyrov.meach.domain.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByReceiverAndSender(User receiver, User sender);
}
