package com.sylphcorps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Column(unique = true)
    private String slug;

    @NotBlank
    @Size(max = 500)
    private String excerpt;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 100)
    private String category;

    @Column(name = "is_published")
    private boolean isPublished = false;

    @Column(name = "is_featured")
    private boolean isFeatured = false;

    @Column(name = "is_trending")
    private boolean isTrending = false;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnore
    private User author;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Article() {}

    public Article(String title, String slug, String excerpt, String content, String category, User author) {
        this.title = title;
        this.slug = slug;
        this.excerpt = excerpt;
        this.content = content;
        this.category = category;
        this.author = author;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public boolean isTrending() { return isTrending; }
    public void setTrending(boolean trending) { isTrending = trending; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper method to increment view count
    public void incrementViewCount() {
        this.viewCount++;
    }
}