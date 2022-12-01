package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemDto,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи.");
        return ResponseEntity.ok(itemService.addItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId,
                                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на редактирование вещи, itemId={}, userId={}", itemId, userId);
        return ResponseEntity.ok(itemService.updateItem(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId,
                                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещи, itemId={}", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping()
    public ResponseEntity<List<ItemDto>> getItemsByUserId(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещей пользователя, userId={}", userId);
        return ResponseEntity.ok(itemService.getItemsByUserId(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Получен запрос на поиск вещей, text={}", text);
        return ResponseEntity.ok(itemService.searchItems(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestBody @Valid CommentDto commentDto,
                                                 @PathVariable Long itemId,
                                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление комментария, itemId={}, userId={}", itemId, userId);
        return ResponseEntity.ok(itemService.addComment(commentDto, itemId, userId));
    }
}
