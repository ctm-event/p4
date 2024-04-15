package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTest {

  @InjectMocks
  private ItemController itemController;

  @Mock
  private ItemRepository items;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getItemById() {
    when(items.findById(1L)).thenReturn(Optional.of(TestHelper.createItem()));
    ResponseEntity<Item> response = itemController.getItemById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    Item item = response.getBody();
    assertNotNull(item);
  }

  @Test
  public void getItems() {
    List<Item> listItems = new ArrayList<Item>();
    listItems.add(TestHelper.createItem());
    when(items.findAll()).thenReturn(listItems);
    ResponseEntity<List<Item>> response = itemController.getItems();

    List<Item> itemList = response.getBody();

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(itemList.size(), 1);
    assertNotNull(itemList);
  }

  @Test
  public void getItemByName() {
    List<Item> items = new ArrayList<>();

    items.add(TestHelper.createItem());
    when(this.items.findByName("ItemX")).thenReturn(items);
    ResponseEntity<List<Item>> response = itemController.getItemsByName("ItemX");

    assertNotNull(response);
    assertEquals(items, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

}