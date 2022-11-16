package ru.sadyrov.meach.services;

import com.auth0.jwt.JWT;
import io.jsonwebtoken.JwtParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sadyrov.meach.domain.Role;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.UserRepository;

import java.util.*;


@Service
public class UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public User loadUserByUsername(String username){
        return userRepository.findByLogin(username);
    }
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;

//    }

    public List<User> getAll() {
        return this.userRepository.findByActiveIs(true);
    }

    public Optional<User> getByLogin(String login) {
        return Optional.ofNullable(this.userRepository.findByLogin(login));
    }

    public boolean addUser(User user) {
        System.out.println(user.getPassword());
        User userFromDb = userRepository.findByLoginAndEmail(user.getLogin(), user.getEmail());
        if (userFromDb != null) {
            System.out.println("exists");
            return false;
        }
        System.out.println("okadd");
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        setActivationCode(user);
        userRepository.save(user);
        return true;
    }
    public boolean activateUser(String activationCode) {
        System.out.println(activationCode.getClass());
        User user = userRepository.findByActivationCode(activationCode);
        if (user == null){
            return false;
        }
        user.setActive(true);
        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }

    private void setActivationCode(User user){
        System.out.println(user);
        user.setActivationCode(UUID.randomUUID().toString());
        if(!user.getEmail().isEmpty()){
            String message = String.format(
                    "Hello, %s \n" +
                            "Welcome to meach! Your activation code: %s",
                    user.getLogin(),
                    user.getActivationCode()
            );
            mailService.sendMessage(user.getEmail(), "Activation", message);
            System.out.println("Email was sent");
        }
    }

    public void getAuthenticatedLogin(String authHeader){
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = authHeader.split(" ")[1].split("\\.");
        String result = new String(decoder.decode(chunks[1]));
        System.out.println(result);
    }

    public boolean sendRequest(String senderLogin, String receiverLogin){
        Optional<User> senderOptional = getByLogin(senderLogin);
        Optional<User> receiverOptional = getByLogin(receiverLogin);
        if(senderOptional.isPresent() && receiverOptional.isPresent()) {
            User sender = senderOptional.get();
            User receiver = receiverOptional.get();
            if(!sender.getSentRequests().contains(receiver) && !receiver.getReceivedRequests().contains(sender)) {
                Set<User> sentRequests = sender.getSentRequests();
                Set<User> receivedRequests = receiver.getReceivedRequests();
                sentRequests.add(receiver);
                receivedRequests.add(sender);
                sender.setSentRequests(sentRequests);
                receiver.setReceivedRequests(receivedRequests);
                userRepository.save(sender);
                userRepository.save(receiver);
                return true;
            }
        }
        return false;
    }

    public boolean addAcceptedUser(String senderLogin, String receiverLogin){
        User sender = userRepository.findByLogin(senderLogin);
        User receiver = userRepository.findByLogin(receiverLogin);
        if(sender!=null && receiver!=null) {
            if (sender.getSentRequests().contains(receiver) && receiver.getReceivedRequests().contains(sender)) {
                sender.getFriends().add(receiver);
                receiver.getFriends().add(sender);
                sender.getSentRequests().remove(receiver);
                receiver.getReceivedRequests().remove(sender);
                userRepository.save(sender);
                userRepository.save(receiver);
                return true;
            }
            return false;
        }
        return false;
    }


//    public List<User> getSentRequests(){
//        List<User> users = userRepository.findBySentRequests();
//        return users;
//    }
}
