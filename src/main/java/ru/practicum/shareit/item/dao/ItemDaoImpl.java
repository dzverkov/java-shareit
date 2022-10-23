package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return items.values().stream().filter(item -> item.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item ->
                        Pattern.compile(String.format("^(.+)%s(.*)$", text.toLowerCase()),
                                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                                .matcher(String.format("%s # %s", item.getName(), item.getDescription()))
                                .matches()
                )
                .collect(Collectors.toList());
    }

}
