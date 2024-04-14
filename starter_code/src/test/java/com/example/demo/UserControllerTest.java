package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private final UserRepository users = mock(UserRepository.class);

  @Mock
  private final CartRepository cartRepository = mock(CartRepository.class);

  @Mock
  private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createUser() throws Exception {
    when(encoder.encode("P@ssw0rd")).thenReturn("HashedPassword");

    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("username");
    userRequest.setPassword("P@ssw0rd");
    userRequest.setConfirmPassword("P@ssw0rd");

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("username", user.getUsername());
    assertEquals("HashedPassword", user.getPassword());
  }

  @Test
  public void findById() {
    User user = TestHelper.createUser();

    when(users.findById(1L)).thenReturn(Optional.of(user));
    ResponseEntity<User> response = userController.findById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(user, response.getBody());
  }

  @Test
  public void findByIdUserNotFound() {
    ResponseEntity<User> response = userController.findById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void findByUsername() {
    User user = TestHelper.createUser();

    when(users.findByUsername("username")).thenReturn(user);
    ResponseEntity<User> response = userController.findByUserName("username");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(user, response.getBody());
    assertEquals(1L, user.getId());
    assertEquals("username", user.getUsername());
    assertEquals("P@ssw0rd", user.getPassword());
  }

  @Test
  public void findByUsernameUserNotFound() {
    ResponseEntity<User> response = userController.findByUserName("username");

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}