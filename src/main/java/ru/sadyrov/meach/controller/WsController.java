//package ru.sadyrov.meach.controller;
//
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.web.bind.annotation.RestController;
//import ru.sadyrov.meach.domain.Article;
//import ru.sadyrov.meach.repository.ArticleRepository;
//import ru.sadyrov.meach.services.ArticleService;
//import ru.sadyrov.meach.services.AuthService;
//import ru.sadyrov.meach.services.UserService;
//
//import java.time.LocalDateTime;
//
//@RestController
//public class WsController {
//
////    private final ArticleService articleService;
//    private final UserService userService;
//    private final AuthService authService;
////    private final ArticleRepository articleRepository;
//
//    public WsController(
////            ArticleService articleService,
//            UserService userService,
//            AuthService authService
//            /*ArticleRepository articleRepository*/) {
////        this.articleService = articleService;
//        this.userService = userService;
//        this.authService = authService;
////        this.articleRepository = articleRepository;
//    }
//
//
////    @MessageMapping("/addArticle")
////    @SendTo("/topic/articles")
////    public Article addArticle(Article article) {
////        System.out.println(article.getUser().getLogin());
////        System.out.println(article.getContent());
////        article.setPostTime(LocalDateTime.now());
////        return articleRepository.save(article);
////    }
//}
