package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                         @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление запроса на вещь.");
        return itemRequestClient.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запросов на вещи, userId={}", userId);
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsFromOtherUsers(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запросов на вещи других пользователей, from = {}, size = {}", from, size);
        return itemRequestClient.getItemRequestsFromOtherUsers(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                             @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запроса на вещь, requestId={}", requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
