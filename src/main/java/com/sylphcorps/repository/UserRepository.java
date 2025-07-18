package com.sylphcorps.repository;

import com.sylphcorps.model.Role;
import com.sylphcorps.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	List<User> findByRole(Role role);

	Page<User> findByIsActiveTrue(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
	Page<User> findByRoleAndIsActiveTrue(Role role, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.fullName LIKE %?1% OR u.username LIKE %?1% OR u.email LIKE %?1%")
	Page<User> findBySearchTerm(String searchTerm, Pageable pageable);
}