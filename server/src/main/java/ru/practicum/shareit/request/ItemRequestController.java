package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                         @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление запроса на вещь.");
        return ResponseEntity.ok(itemRequestService.addItemRequest(itemRequestDto, userId));
    }

    @GetMapping()
    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запросов на вещи, userId={}", userId);
        return ResponseEntity.ok(itemRequestService.getItemRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsFromOtherUsers(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запросов на вещи других пользователей, from = {}, size = {}", from, size);
        return ResponseEntity.ok(itemRequestService.getItemRequestsFromOtherUsers(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@PathVariable Long requestId,
                                                             @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение запроса на вещь, requestId={}", requestId);
        return ResponseEntity.ok(itemRequestService.getItemRequestById(requestId, userId));
    }
}
