package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequests(Long userId);

    List<ItemRequestDto> getItemRequestsFromOtherUsers(Integer from, Integer size, Long userId);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);
}
