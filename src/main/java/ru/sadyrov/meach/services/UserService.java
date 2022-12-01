package ru.sadyrov.meach.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sadyrov.meach.domain.Role;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.UserRepository;

import java.util.*;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;


    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public User loadUserByUsername(String username) {
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

    public Optional<User> getByMail(String mail) {
        return Optional.ofNullable(this.userRepository.findByEmail(mail));
    }

    public void addUser(User user) {
        System.out.println(user.getPassword());
//        User userFromDb = userRepository.findByLoginAndEmail(user.getLogin(), user.getEmail());
//        if (userFromDb != null) {
//            System.out.println("exists");
//            return false;
//        }
        System.out.println("okadd");
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        setActivationCode(user);
        userRepository.save(user);
//        return true;
    }

    public void resetPassword(User user) {
        setActivationCode(user);
        userRepository.save(user);
    }

    public boolean setPassword(String activationCode, String password) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public boolean activateUser(String activationCode) {
        System.out.println(activationCode);
        User user = userRepository.findByActivationCode(activationCode);
        if (user == null) {
            System.out.println("Not found");
            return false;
        }
        user.setActive(true);
        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }

    private void setActivationCode(User user) {
        System.out.println(user);
        user.setActivationCode(UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 5));
        if (!user.getEmail().isEmpty()) {
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

    public void getAuthenticatedLogin(String authHeader) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = authHeader.split(" ")[1].split("\\.");
        String result = new String(decoder.decode(chunks[1]));
        System.out.println(result);
    }

    public boolean sendRequest(String senderLogin, String receiverLogin) {
        Optional<User> senderOptional = getByLogin(senderLogin);
        Optional<User> receiverOptional = getByLogin(receiverLogin);
        if (senderOptional.isPresent() && receiverOptional.isPresent()) {
            User sender = senderOptional.get();
            User receiver = receiverOptional.get();
            if (!sender.getSentRequests().contains(receiver) && !receiver.getReceivedRequests().contains(sender)) {
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

    public boolean addAcceptedUser(String senderLogin, String receiverLogin) {
        User sender = userRepository.findByLogin(senderLogin);
        User receiver = userRepository.findByLogin(receiverLogin);
        if (sender != null && receiver != null) {
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

    public boolean deleteFriend(User user, String DeletedUserid) {
        Set<User> friends = user.getFriends();
        User deletedUser = userRepository.findByLogin(DeletedUserid);
        if (deletedUser != null) {
            friends.remove(deletedUser);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean changeStatus(String login, boolean status) {
        System.out.println(login);
        Optional<User> userOptional = getByLogin(login);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setReadyToMeet(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public JSONArray createUsersJson(List<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("secondName", user.getSecondName());
            jsonObject.put("login", user.getLogin());
            jsonObject.put("description", user.getDescription());
            jsonObject.put("readyToMeet", user.isReadyToMeet());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public JSONArray createUsersJson(Set<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("secondName", user.getSecondName());
            jsonObject.put("login", user.getLogin());
            jsonObject.put("description", user.getDescription());
            jsonObject.put("readyToMeet", user.isReadyToMeet());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public JSONObject createUserJson(User user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", user.getFirstName());
        jsonObject.put("secondName", user.getSecondName());
        jsonObject.put("login", user.getLogin());
        jsonObject.put("description", user.getDescription());
        jsonObject.put("readyToMeet", user.isReadyToMeet());
        return jsonObject;
    }

    public List<User> getByDescription(String substring) {
        return userRepository.findByDescription(substring);
    }

    public List<User> getBySubFirstName(String substring) {
        return userRepository.findBySubFirstName(substring);
    }

    public List<User> getBySubSecondName(String substring) {
        return userRepository.findBySubSecondName(substring);
    }

    public List<User> getBySubLogin(String substring) {
        return userRepository.findBySubLogin(substring);
    }

    public User setDescription(String login, String description) {
        User user = userRepository.findByLogin(login);
        user.setDescription(description);
        userRepository.save(user);
        return user;
    }

    public List<User> getReadyToMeetUsers() {
        return userRepository.findByReadyToMeet(true);
    }
}
