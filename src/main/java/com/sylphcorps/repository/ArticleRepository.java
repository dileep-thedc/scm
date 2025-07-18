package com.sylphcorps.repository;

import com.sylphcorps.model.Article;
import com.sylphcorps.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findBySlug(String slug);

    Boolean existsBySlug(String slug);

    Page<Article> findByIsPublishedTrue(Pageable pageable);

    Page<Article> findByIsPublishedTrueAndIsFeaturedTrue(Pageable pageable);

    Page<Article> findByIsPublishedTrueAndIsTrendingTrue(Pageable pageable);

    Page<Article> findByIsPublishedTrueAndCategory(String category, Pageable pageable);

    Page<Article> findByAuthor(User author, Pageable pageable);

    Page<Article> findByAuthorAndIsPublishedTrue(User author, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.isPublished = true AND (a.title LIKE %?1% OR a.content LIKE %?1%)")
    Page<Article> findBySearchTerm(String searchTerm, Pageable pageable);

    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.isPublished = true AND a.category IS NOT NULL")
    List<String> findDistinctCategories();

    @Query("SELECT a FROM Article a WHERE a.isPublished = true ORDER BY a.viewCount DESC")
    Page<Article> findMostViewedArticles(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.isPublished = true ORDER BY a.publishedAt DESC")
    Page<Article> findLatestArticles(Pageable pageable);

    @Query("SELECT COUNT(a) FROM Article a WHERE a.author = ?1")
    Long countByAuthor(User author);

    @Query("SELECT COUNT(a) FROM Article a WHERE a.author = ?1 AND a.isPublished = true")
    Long countByAuthorAndIsPublishedTrue(User author);
}