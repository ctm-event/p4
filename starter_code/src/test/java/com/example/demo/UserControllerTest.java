package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  
  @Mock
  private UserRepository users;

  @Mock
  private CartRepository cartRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createUser() throws Exception {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("username");
    userRequest.setPassword("P@ssw0rd");
    userRequest.setConfirmPassword("P@ssw0rd");

    when(bCryptPasswordEncoder.encode("P@ssw0rd")).thenReturn("HashedPassword");
    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    User user = response.getBody();
    assertNotNull(user);
    assertEquals(user.getId(), 0);
    assertEquals(user.getUsername(), "username");

    assertEquals(user.getPassword(), "HashedPassword");
  }

  @Test
  public void createUserBadPasswordLength() throws Exception {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("username");
    userRequest.setPassword("P@ssw0r");
    userRequest.setConfirmPassword("P@ssw0r");

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    assertEquals(response.getBody(), null);
  }

  @Test
  public void createUserPasswordMissMatch() throws Exception {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("username");
    userRequest.setPassword("P@ssw0rd");
    userRequest.setConfirmPassword("P@ssw0rd1");

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    assertEquals(response.getBody(), null);
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
    assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    assertEquals(response.getBody(), null);
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