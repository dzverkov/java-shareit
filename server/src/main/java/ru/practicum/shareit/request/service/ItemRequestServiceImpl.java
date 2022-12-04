package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        // Проверка наличия пользователя
        User user = getUser(userId);

        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);

        itemRequest.setRequester(user);
        return mapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Long userId) {

        // Проверка наличия пользователя
        User user = getUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(user.getId());
        return itemRequests.stream().map(itemRequest -> {
            ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest);
            itemRequestDto.setItems(getRequestItems(itemRequest.getId()));
            return itemRequestDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getItemRequestsFromOtherUsers(Integer from, Integer size, Long userId) {

        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester_IdIsNot(
                userId,
                pageRequest
        );

        return itemRequests.stream().map(itemRequest -> {
            ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest);
            itemRequestDto.setItems(getRequestItems(itemRequest.getId()));
            return itemRequestDto;
        }).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {

        // Проверка наличия пользователя
        getUser(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(
                        String.format("Запрос на вещь requestId = %d от пользователя userId = %d не найден",
                                requestId,
                                userId))
                );

        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(getRequestItems(itemRequest.getId()));
        return itemRequestDto;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь userId = %d не найден.", userId))
        );
    }

    private List<ItemRequestDto.RequestItem> getRequestItems(Long requestId) {

        List<Item> items = itemRepository.findAllByRequest(requestId);

        return items.stream().map(mapper::toItemRequestDtoItem).collect(Collectors.toList());
    }
}
