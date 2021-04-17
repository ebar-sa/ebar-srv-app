package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.User;
import com.ebarapp.ebar.model.type.RoleType;
import com.ebarapp.ebar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceMockedTests {

    private static final String USERNAME = "jorgedz";
    private static final String PASSWORD = "sample";

    @Mock
    private UserRepository userRepositoryMocked;

    private User user;
    private Set<RoleType> roles;

    private UserService userServiceMocked;

    @BeforeEach
    void setupMock() {
        this.roles = new HashSet<>();
        this.roles.add(RoleType.ROLE_CLIENT);
        this.user = new User();
        this.user.setUsername(USERNAME);
        this.user.setFirstName("Jorge");
        this.user.setLastName("Diaz");
        this.user.setEmail("example@mail.com");
        this.user.setDni("34235645X");
        this.user.setPhoneNumber("654321678");
        this.user.setPassword(PASSWORD);
        this.user.setRoles(this.roles);
        this.user.setStripeId("id");

        this.userServiceMocked = new UserService(userRepositoryMocked);
    }

    @Test
    void shouldFindUserByUsername() {
        when(this.userRepositoryMocked.findByUsername(USERNAME)).thenReturn(Optional.of(this.user));
        UserDetails userByUsername = this.userServiceMocked.loadUserByUsername(USERNAME);
        assertThat(userByUsername).hasNoNullFieldsOrProperties();
        assertThat(userByUsername.getUsername()).isEqualTo(USERNAME);
        assertThat(userByUsername.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void shouldThrowUsernameNotFoundException() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> this.userServiceMocked.loadUserByUsername("wrong"))
                .withMessage("Usuario no encontrado");
    }

}
