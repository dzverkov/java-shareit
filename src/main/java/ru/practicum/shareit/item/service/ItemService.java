package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> searchItems(String text);
}
