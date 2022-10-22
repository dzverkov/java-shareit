package ru.practicum.shareit.item.service;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUpdateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemDao itemDao;
    private UserDao userDao;
    private long id = 0;

    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {

        User user = getUser(userId);
        validateItem(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(getNextId());
        item.setOwner(user.getId());
        return ItemMapper.toItemDto(itemDao.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {

        User user = getUser(userId);
        Item existItem = itemDao.getItemById(itemId);

        if (user.getId() != existItem.getOwner()) {
            throw new ItemUpdateException(
                    String.format("Пользователь с id = %d не является владельцем вещи с id = %d", userId, itemId)
            );
        }
        mergeFields(itemDto, existItem);

        validateItem(ItemMapper.toItemDto(existItem));
        return ItemMapper.toItemDto(itemDao.updateItem(existItem));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemDao.getItemById(itemId);
        if (item == null) {
            throw new ItemNotFoundException(String.format("Вещь с id = %d не найдена.", itemId));
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        User user = getUser(userId);

        List<Item> items = itemDao.getItemsByUserId(user.getId());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemDao.searchItems(text);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void validateItem(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Значение name не задано или пустое.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Значение description не задано или пустое.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Значение available = null.");
        }

    }

    private long getNextId() {
        return ++id;
    }

    private User getUser(Long userId) {
        if (userId == null || userId < 0) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        User user = userDao.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return user;
    }

    private void mergeFields(ItemDto source, Item target) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getAvailable() != null) target.setAvailable(source.getAvailable());
    }
}
