package ru.sadyrov.meach.services;

import org.springframework.stereotype.Service;
import ru.sadyrov.meach.domain.Article;
import ru.sadyrov.meach.domain.User;
import ru.sadyrov.meach.repository.ArticleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void addPost(Article article) {
        articleRepository.save(article);
    }

    public List<Article> getAllPosts() {
        return this.articleRepository.findAll();
    }

    public List<Article> getPostByUser(Optional<User> user){
        return articleRepository.findByUser(user);
    }

    public void deleteArticle(Article article){
        articleRepository.delete(article);
    }
}
