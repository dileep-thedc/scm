package com.sylphcorps.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.*;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 50)
	@Column(unique = true)
	private String username;

	@NotBlank
	@Size(max = 100)
	@Email
	@Column(unique = true)
	private String email;

	@NotBlank
	@Size(max = 120)
	@JsonIgnore
	private String password;

	@NotBlank
	@Size(max = 100)
	private String fullName;

	@Size(max = 500)
	private String bio;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private Role role = Role.USER;

	@Column(name = "is_active")
	private boolean isActive = true;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Article> articles = new ArrayList<>();

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Constructors
	public User() {}

	public User(String username, String email, String password, String fullName, Role role) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.role = role;
	}

	// Getters and Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getFullName() { return fullName; }
	public void setFullName(String fullName) { this.fullName = fullName; }

	public String getBio() { return bio; }
	public void setBio(String bio) { this.bio = bio; }

	public Role getRole() { return role; }
	public void setRole(Role role) { this.role = role; }

	public boolean isActive() { return isActive; }
	public void setActive(boolean active) { isActive = active; }

	public List<Article> getArticles() { return articles; }
	public void setArticles(List<Article> articles) { this.articles = articles; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}