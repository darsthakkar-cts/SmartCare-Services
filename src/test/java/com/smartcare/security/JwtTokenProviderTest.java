package com.smartcare.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String jwtSecret = "testSecretKeyForJwtTokenGenerationThatIsLongEnoughForHS256Algorithm";
    private final int jwtExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", jwtExpiration);
    }

    @Test
    @DisplayName("Should generate JWT token")
    void shouldGenerateJwtToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = UserPrincipal.create(createTestUser());
        
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        // When
        String token = jwtTokenProvider.generateToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ")); // JWT tokens start with eyJ
    }

    @Test
    @DisplayName("Should get user id from token")
    void shouldGetUserIdFromToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = UserPrincipal.create(createTestUser());
        
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        String token = jwtTokenProvider.generateToken(authentication);

        // When
        Long userId = jwtTokenProvider.getUserIdFromJWT(token);

        // Then
        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = UserPrincipal.create(createTestUser());
        
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        String token = jwtTokenProvider.generateToken(authentication);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate malformed token")
    void shouldInvalidateMalformedToken() {
        // Given
        String malformedToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should invalidate empty token")
    void shouldInvalidateEmptyToken() {
        // When
        boolean isValid = jwtTokenProvider.validateToken("");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should invalidate null token")
    void shouldInvalidateNullToken() {
        // When
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    private com.smartcare.model.User createTestUser() {
        com.smartcare.model.User user = new com.smartcare.model.User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(java.util.Set.of(com.smartcare.model.Role.USER));
        return user;
    }
}
