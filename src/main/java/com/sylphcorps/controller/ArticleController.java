// ArticleController.java
package com.sylphcorps.controller;

import com.sylphcorps.dto.ArticleDTO;
import com.sylphcorps.model.Article;
import com.sylphcorps.model.User;
import com.sylphcorps.security.UserDetailsImpl;
import com.sylphcorps.serviceImpl.ArticleService;
import com.sylphcorps.serviceImpl.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    // Public endpoints
    @GetMapping("/public")
    public ResponseEntity<Page<Article>> getAllPublishedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(articleService.getAllPublishedArticles(pageable));
    }

    @GetMapping("/public/{slug}")
    public ResponseEntity<Article> getArticleBySlug(@PathVariable String slug) {
        return articleService.findBySlug(slug)
                .map(article -> ResponseEntity.ok().body(article))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{slug}/view")
    public ResponseEntity<Article> incrementViewCount(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.incrementViewCount(slug));
    }

    @GetMapping("/featured")
    public ResponseEntity<Page<Article>> getFeaturedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return ResponseEntity.ok(articleService.getFeaturedArticles(pageable));
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<Article>> getTrendingArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return ResponseEntity.ok(articleService.getTrendingArticles(pageable));
    }

    @GetMapping("/latest")
    public ResponseEntity<Page<Article>> getLatestArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(articleService.getLatestArticles(pageable));
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<Page<Article>> getMostViewedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(articleService.getMostViewedArticles(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Article>> getArticlesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return ResponseEntity.ok(articleService.getArticlesByCategory(category, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(articleService.getAllCategories());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Article>> searchArticles(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return ResponseEntity.ok(articleService.searchArticles(q, pageable));
    }

    // Author endpoints
    @PostMapping
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User author = userService.findById(userDetails.getId());

        Article article = articleService.createArticle(articleDTO, author.getUsername());
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO articleDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Article article = articleService.updateArticle(id, articleDTO, userDetails.getUsername());
        return ResponseEntity.ok(article);
    }

    @GetMapping("/my-articles")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<Article>> getMyArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User author = userService.findById(userDetails.getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(articleService.getArticlesByAuthor(author, pageable));
    }
}