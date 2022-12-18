package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
