//package ru.sadyrov.meach.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import ru.sadyrov.meach.domain.Article;
//import ru.sadyrov.meach.domain.User;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ArticleRepository extends JpaRepository<Article, Long> {
//    List<Article> findByUser(Optional<User> user);
//
//    Article findById(int id);
//}
