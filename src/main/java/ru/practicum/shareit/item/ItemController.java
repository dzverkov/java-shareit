package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи.");
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на редактирование вещи, itemId={}, userId={}", itemId, userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещи, itemId={}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getItemsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение вещей пользователя, userId={}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск вещей, text={}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody @Valid CommentDto commentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление комментария, itemId={}, userId={}", itemId, userId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
