package ru.sadyrov.meach.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sadyrov.meach.domain.Article;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.ArticleRepository;
import ru.sadyrov.meach.services.ArticleService;
import ru.sadyrov.meach.services.AuthService;
import ru.sadyrov.meach.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("posts")
@CrossOrigin(origins = {"http://localhost:8080", "http://10.17.33.199:8080/"})
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private final AuthService authService;
    private final ArticleRepository articleRepository;

    public ArticleController(
            ArticleService articleService,
            UserService userService,
            AuthService authService, ArticleRepository articleRepository) {
        this.articleService = articleService;
        this.userService = userService;
        this.authService = authService;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/getall")
    public List<Article> getPosts() {
        return articleRepository.findAll();
    }

    @GetMapping("/getPosts/{login}")
    public List<Article> getPostByUser(@PathVariable(value = "login") String login) {
        return articleService.getPostByUser(userService.getByLogin(login));
    }

    @PostMapping("/addPost")
    public void addPost(@RequestBody Article article, @AuthenticationPrincipal User user) {
        article.setPostTime(LocalDateTime.now());
        article.setUser(user);
        articleService.addPost(article);

    }

    @DeleteMapping("/deleteArticle")
    public void deletePost(@RequestBody Article article) {
        articleService.deleteArticle(article);
    }
}

