package com.sylphcorps.serviceImpl;



import com.sylphcorps.dto.ArticleDTO;
import com.sylphcorps.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.sylphcorps.model.Article;
import com.sylphcorps.model.User;
import com.sylphcorps.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserService userService;

    public Article createArticle(ArticleDTO articleDTO, String username) {
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setSlug(generateSlug(articleDTO.getTitle()));
        article.setExcerpt(articleDTO.getExcerpt());
        article.setContent(articleDTO.getContent());
        article.setImageUrl(articleDTO.getImageUrl());
        article.setCategory(articleDTO.getCategory());
        article.setPublished(articleDTO.isPublished());
        article.setFeatured(articleDTO.isFeatured());
        article.setTrending(articleDTO.isTrending());
        article.setAuthor(author);

        if (articleDTO.isPublished()) {
            article.setPublishedAt(LocalDateTime.now());
        }

        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, ArticleDTO articleDTO, String username) {
        Article article = findById(id);

        // Check if user is the author or admin
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!article.getAuthor().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("You don't have permission to update this article");
        }

        article.setTitle(articleDTO.getTitle());
        article.setSlug(generateSlug(articleDTO.getTitle()));
        article.setExcerpt(articleDTO.getExcerpt());
        article.setContent(articleDTO.getContent());
        article.setImageUrl(articleDTO.getImageUrl());
        article.setCategory(articleDTO.getCategory());

        // Only update published status if it's changing from false to true
        if (!article.isPublished() && articleDTO.isPublished()) {
            article.setPublishedAt(LocalDateTime.now());
        }

        article.setPublished(articleDTO.isPublished());
        article.setFeatured(articleDTO.isFeatured());
        article.setTrending(articleDTO.isTrending());

        return articleRepository.save(article);
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
    }

    public Optional<Article> findBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    public Article getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with slug: " + slug));
    }

    public Page<Article> getPublishedArticles(Pageable pageable) {
        return articleRepository.findByIsPublishedTrue(pageable);
    }

    public Page<Article> getFeaturedArticles(Pageable pageable) {
        return articleRepository.findByIsPublishedTrueAndIsFeaturedTrue(pageable);
    }

    public Page<Article> getTrendingArticles(Pageable pageable) {
        return articleRepository.findByIsPublishedTrueAndIsTrendingTrue(pageable);
    }

    public Page<Article> getLatestArticles(Pageable pageable) {
        return articleRepository.findLatestArticles(pageable);
    }

    public Page<Article> getMostViewedArticles(Pageable pageable) {
        return articleRepository.findMostViewedArticles(pageable);
    }

    public Page<Article> getArticlesByCategory(String category, Pageable pageable) {
        return articleRepository.findByIsPublishedTrueAndCategory(category, pageable);
    }

    public Page<Article> getArticlesByAuthor(User author, Pageable pageable) {
        return articleRepository.findByAuthor(author, pageable);
    }

    public Page<Article> getPublishedArticlesByAuthor(User author, Pageable pageable) {
        return articleRepository.findByAuthorAndIsPublishedTrue(author, pageable);
    }

    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    public Page<Article> searchArticles(String searchTerm, Pageable pageable) {
        return articleRepository.findBySearchTerm(searchTerm, pageable);
    }

    public List<String> getAllCategories() {
        return articleRepository.findDistinctCategories();
    }

    public Article publishArticle(Long id) {
        Article article = findById(id);
        article.setPublished(true);
        article.setPublishedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Article unpublishArticle(Long id) {
        Article article = findById(id);
        article.setPublished(false);
        return articleRepository.save(article);
    }

    public Article setFeatured(Long id, boolean featured) {
        Article article = findById(id);
        article.setFeatured(featured);
        return articleRepository.save(article);
    }

    public Article setTrending(Long id, boolean trending) {
        Article article = findById(id);
        article.setTrending(trending);
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        Article article = findById(id);
        articleRepository.delete(article);
    }

    public Article incrementViewCount(String slug) {
        Article article = getArticleBySlug(slug);
        article.incrementViewCount();
        return articleRepository.save(article);
    }

    public Page<Article> getAllPublishedArticles(Pageable pageable) {
        return articleRepository.findByIsPublishedTrue(pageable);
    }

    public Long getArticleCountByAuthor(User author) {
        return articleRepository.countByAuthor(author);
    }

    public Long getPublishedArticleCountByAuthor(User author) {
        return articleRepository.countByAuthorAndIsPublishedTrue(author);
    }

    public long getTotalArticleCount() {
        return articleRepository.count();
    }

    public long getPublishedArticleCount() {
        return articleRepository.findByIsPublishedTrue(Pageable.unpaged()).getTotalElements();
    }

    private String generateSlug(String title) {
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        // Ensure slug is unique
        String originalSlug = slug;
        int counter = 1;
        while (articleRepository.existsBySlug(slug)) {
            slug = originalSlug + "-" + counter++;
        }

        return slug;
    }
}