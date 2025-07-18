package com.sylphcorps.serviceImpl;

import com.sylphcorps.dto.SignupRequest;
import com.sylphcorps.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.sylphcorps.model.Role;
import com.sylphcorps.model.User;
import com.sylphcorps.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(SignupRequest signupRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getFullName(),
                signupRequest.getRole()
        );

        user.setBio(signupRequest.getBio());
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getActiveUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable);
    }

    public Page<User> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRoleAndIsActiveTrue(role, pageable);
    }

    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findBySearchTerm(searchTerm, pageable);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, User userDetails) {
        User user = findById(id);

        user.setFullName(userDetails.getFullName());
        user.setBio(userDetails.getBio());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user);
    }

    public User updateUserRole(Long id, Role role) {
        User user = findById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = findById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}