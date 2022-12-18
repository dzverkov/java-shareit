package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи.");
        validateItemNew(itemDto);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId,
                                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на редактирование вещи, itemId={}, userId={}", itemId, userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещи, itemId={}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemsByUserId(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещей пользователя, userId={}", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Получен запрос на поиск вещей, text={}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDto commentDto,
                                                 @PathVariable Long itemId,
                                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление комментария, itemId={}, userId={}", itemId, userId);
        return itemClient.addComment(commentDto, itemId, userId);
    }

    private void validateItemNew(ItemDto item) {
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
}
