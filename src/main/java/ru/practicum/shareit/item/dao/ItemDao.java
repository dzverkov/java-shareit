package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(Long itemId);

    List<Item> getItemsByUserId(Long userId);

    List<Item> searchItems(String text);
}
