package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

public class CartControllerTest {
  private User user;
  private Item item;
  private Cart cart;

  @InjectMocks
  private CartController cartController;

  @Mock
  private CartRepository cartRepository;

  @Mock
  private UserRepository users;

  @Mock
  private ItemRepository items;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void addToCart() {
    initMockData();
  
    ModifyCartRequest modifyCartRequest = TestHelper.createModifyCartRequest("username", 1, 1);
    ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
    assertNotNull(responseEntity);
    assertEquals(200, responseEntity.getStatusCodeValue());
    Cart responseCart = responseEntity.getBody();
    assertNotNull(responseCart);
    List<Item> items = responseCart.getItems();
    assertNotNull(items);
    assertEquals("username", responseCart.getUser().getUsername());
    verify(cartRepository, times(1)).save(responseCart);
  }

  @Test
  public void addToCartUserNotFoundError() {
    ModifyCartRequest modifyCartRequest = TestHelper.createModifyCartRequest("", 1, 1);
    ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
    assertNotNull(responseEntity);
    assertEquals(responseEntity.getBody(), null);
    assertEquals(responseEntity.getStatusCodeValue(), 404);
  }

  @Test
  public void addToCartEmptyCartError() {
    when(users.findByUsername("username")).thenReturn(new User());

    ModifyCartRequest modifyCartRequest = TestHelper.createModifyCartRequest("username", 1, 1);
    ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
    assertNotNull(responseEntity);
    
    verify(items, times(1)).findById(1L);
    verify(users, times(1)).findByUsername("username");
    assertEquals(404, responseEntity.getStatusCodeValue());
  }

  @Test
  public void removeFromCart() {
    initMockData();

    ModifyCartRequest modifyCartRequest = TestHelper.createModifyCartRequest("username", 1, 1);
    ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

    assertNotNull(responseEntity);
    assertEquals(200, responseEntity.getStatusCodeValue());

    Cart responseCart = responseEntity.getBody();
    assertNotNull(responseCart);

    List<Item> items = responseCart.getItems();
    assertEquals(0, items.size());
    assertNotNull(items);
    assertEquals("username", responseCart.getUser().getUsername());

    verify(users, times(1)).findByUsername("username");
    verify(cartRepository, times(1)).save(responseCart);
  }

  @Test
  public void removeFromCartUserNotFoundError() {

    ModifyCartRequest modifyCartRequest = TestHelper.createModifyCartRequest("", 1, 1);
    ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

    assertNotNull(responseEntity);
    assertEquals(404, responseEntity.getStatusCodeValue());
  }

  @Test
  public void removeFromCartEmptyCartError() {

    when(users.findByUsername("username")).thenReturn(new User());
    // when(items.findById(1L)).thenReturn(Optional.empty());

    ModifyCartRequest modifyCartRequest = TestHelper.createModifyCartRequest("username", 1, 1);
    ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

    assertNotNull(responseEntity);
    verify(items, times(1)).findById(1L);
    verify(users, times(1)).findByUsername("username");
    assertEquals(404, responseEntity.getStatusCodeValue());
  }

  private void initMockData() {
    user = TestHelper.createUser();
    item = TestHelper.createItem();
    cart = user.getCart();
    cart.addItem(item);
    cart.setUser(user);
    user.setCart(cart);
    when(items.findById(1L)).thenReturn(Optional.of(item));
    when(users.findByUsername("username")).thenReturn(user);
  }
}