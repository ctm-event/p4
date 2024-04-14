package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTest {
  private User user;
  private Item item;
  private Cart cart;

  @InjectMocks
  private OrderController orderController;

  @Mock
  private final UserRepository users = mock(UserRepository.class);

  @Mock
  private final OrderRepository orders = mock(OrderRepository.class);

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void submit() {
    setUp();

    List<Item> itemList = new ArrayList<>();
    itemList.add(item);
    cart.setItems(itemList);

    cart.setUser(user);
    user.setCart(cart);

    when(users.findByUsername("username")).thenReturn(user);

    ResponseEntity<UserOrder> response = orderController.submit("username");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    UserOrder retrievedUserOrder = response.getBody();
    assertNotNull(retrievedUserOrder);
    assertNotNull(retrievedUserOrder.getItems());
    assertNotNull(retrievedUserOrder.getTotal());
    assertNotNull(retrievedUserOrder.getUser());
  }

  @Test
  public void submitUserNotFoundError() {
    setUp();

    cart.setUser(user);
    user.setCart(cart);

    List<Item> itemList = new ArrayList<>();
    itemList.add(item);
    cart.setItems(itemList);

    when(users.findByUsername("username")).thenReturn(null);
    ResponseEntity<UserOrder> response = orderController.submit("username");

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void ordersByUser() {
    setUp();

    List<Item> itemList = new ArrayList<>();
    itemList.add(item);
    cart.setItems(itemList);

    cart.setUser(user);
    user.setCart(cart);

    when(users.findByUsername("username")).thenReturn(user);
    orderController.submit("username");

    ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("username");

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    List<UserOrder> userOrders = responseEntity.getBody();
    assertNotNull(userOrders);
    assertEquals(0, userOrders.size());
  }

  @Test
  public void ordersByUserNotFound() {
    setUp();

    cart.setUser(user);
    user.setCart(cart);

    List<Item> itemList = new ArrayList<>();
    itemList.add(item);
    cart.setItems(itemList);

    when(users.findByUsername("username")).thenReturn(null);

    orderController.submit("username");

    ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("username");

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  private void setUp() {
    user = TestHelper.createUser();
    item = TestHelper.createItem();
    cart = user.getCart();
  }
}