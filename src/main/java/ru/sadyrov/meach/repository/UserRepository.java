package ru.sadyrov.meach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import ru.sadyrov.meach.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    User findByEmail(String email);

    List<User> findByActiveIs(boolean isActive);

    User findByActivationCode(String activationCode);

    User findByLoginAndEmail(String login, String email);

//    List<User> findBySentRequests();
}