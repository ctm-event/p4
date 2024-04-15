package com.example.demo;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;

public class TestHelper {
  public static User createUser() {
    User user = new User();
    user.setId(1);
    user.setUsername("username");
    user.setPassword("P@ssw0rd");

    Cart cart = new Cart();
    cart.setId(1L);
    cart.setItems(new ArrayList<Item>());
    cart.setTotal(BigDecimal.valueOf(0.0));
    user.setCart(cart);

    return user;
  }

  public static Item createItem() {
    Item item = new Item();
    item.setId(1L);
    item.setName("Created Item");
    item.setDescription("This is fake item.");
    item.setPrice(BigDecimal.valueOf(177));
    return item;
  }

  public static ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity) {
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setUsername(username);
    modifyCartRequest.setItemId(itemId);
    modifyCartRequest.setQuantity(quantity);
    return modifyCartRequest;
  }
}
