package com.sylphcorps.dto;

import javax.validation.constraints.*;


public class ArticleDTO {
    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String excerpt;

    @NotBlank
    private String content;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 100)
    private String category;

    private boolean isPublished = false;
    private boolean isFeatured = false;
    private boolean isTrending = false;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

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
}