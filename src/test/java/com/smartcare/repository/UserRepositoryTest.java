package com.smartcare.repository;

import com.smartcare.model.Role;
import com.smartcare.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRoles(new HashSet<>(Set.of(Role.USER)));

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRoles(new HashSet<>(Set.of(Role.USER, Role.DOCTOR)));

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // When
        Optional<User> found = userRepository.findByUsername("user1");

        // Then
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUsername());
        assertEquals("user1@example.com", found.get().getEmail());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("user2@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("user2", found.get().getUsername());
        assertEquals("user2@example.com", found.get().getEmail());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // When & Then
        assertTrue(userRepository.existsByUsername("user1"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // When & Then
        assertTrue(userRepository.existsByEmail("user1@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should save new user")
    void shouldSaveNewUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setRoles(new HashSet<>(Set.of(Role.USER)));

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals("newuser@example.com", saved.getEmail());

        // Verify it's actually in the database
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("newuser", found.get().getUsername());
    }

    @Test
    @DisplayName("Should update existing user")
    void shouldUpdateExistingUser() {
        // Given
        User existingUser = userRepository.findByUsername("user1").get();
        existingUser.setFirstName("Updated");
        existingUser.setLastName("Name");

        // When
        User updated = userRepository.save(existingUser);

        // Then
        assertEquals("Updated", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("user1", updated.getUsername()); // Should remain unchanged

        // Verify it's actually updated in the database
        Optional<User> found = userRepository.findById(existingUser.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getFirstName());
        assertEquals("Name", found.get().getLastName());
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // Given
        User userToDelete = userRepository.findByUsername("user1").get();
        Long userId = userToDelete.getId();

        // When
        userRepository.delete(userToDelete);

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
        assertFalse(userRepository.existsByUsername("user1"));
    }
}
