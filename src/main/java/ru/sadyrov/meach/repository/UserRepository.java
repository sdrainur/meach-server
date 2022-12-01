package ru.sadyrov.meach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sadyrov.meach.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);

    User findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findByActiveIs(boolean isActive);

    User findByActivationCode(String activationCode);

    User findByLoginAndEmail(String login, String email);

    List<User> findByReadyToMeet(boolean readyToMeet);

    @Query(value = "SELECT * FROM usr u WHERE u.description LIKE %:substring%", nativeQuery = true)
    List<User> findByDescription(@Param("substring") String substring);

    @Query(value = "SELECT * FROM usr u WHERE u.first_name LIKE %:substring%", nativeQuery = true)
    List<User> findBySubFirstName(@Param("substring") String substring);
    @Query(value = "SELECT * FROM usr u WHERE u.second_name LIKE %:substring%", nativeQuery = true)
    List<User> findBySubSecondName(@Param("substring") String substring);
    @Query(value = "SELECT * FROM usr u WHERE u.login LIKE %:substring%", nativeQuery = true)
    List<User> findBySubLogin(@Param("substring") String substring);
}