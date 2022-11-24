package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    private List<User> users;
    private List<Item> items;

    private List<ItemRequest> requests;

    private ItemRepository itemRepository;

    private ItemRequestService itemRequestService;

    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void init() {
        users = Arrays.asList(
                new User(1L, "User1", "user1@mail.ru"),
                new User(2L, "User2", "user2@mail.ru"),
                new User(3L, "User3", "user3@mail.ru")
        );

        items = Arrays.asList(
                new Item(1L, "Item1", "Item 1 description", true, 1L, null),
                new Item(2L, "Item2", "Item 2 description", true, 2L, 2L),
                new Item(3L, "Item3", "Item 3 description", true, 3L, 1L)
        );

        requests = Arrays.asList(
                new ItemRequest(1L, "Request 1", users.get(0), LocalDateTime.now()),
                new ItemRequest(2L, "Request 2", users.get(2), LocalDateTime.now())
        );


        itemRepository = mock(ItemRepository.class);
        when(itemRepository.findById(items.get(0).getId())).thenReturn(Optional.of(items.get(0)));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(users.get(1)));
        when(userRepository.findById(3L)).thenReturn(Optional.of(users.get(2)));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        itemRequestRepository = mock(ItemRequestRepository.class);

        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemRepository);
    }

    @Test
    void addItemRequest() {

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                requests.get(0).getId(),
                requests.get(0).getDescription(),
                requests.get(0).getRequester().getId(),
                requests.get(0).getCreated(),
                null
        );

        when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(requests.get(0));

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.addItemRequest(itemRequestDto, 10L));


        ItemRequestDto itemRequestDtoRes = itemRequestService.addItemRequest(
                itemRequestDto,
                requests.get(0).getRequester().getId()
        );

        assertEquals(itemRequestDto.getId(), itemRequestDtoRes.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoRes.getDescription());
        assertEquals(itemRequestDto.getRequester(), itemRequestDtoRes.getRequester());

    }

    @Test
    void getItemRequests() {
        when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(users.get(0).getId()))
                .thenReturn(Collections.singletonList(requests.get(0)));

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequests(10L));

        List<ItemRequestDto>  itemRequests = itemRequestService.getItemRequests(users.get(0).getId());

        assertEquals(requests.get(0).getId(), itemRequests.get(0).getId());
        assertEquals(requests.get(0).getDescription(), itemRequests.get(0).getDescription());
        assertEquals(requests.get(0).getRequester().getId(), itemRequests.get(0).getRequester());
    }

    @Test
    void getItemRequestsFromOtherUsers() {
        PageImpl pagedRes = new PageImpl(Collections.singletonList(requests.get(1)));
        when(itemRequestRepository.findAllByRequester_IdIsNotOrderByCreatedDesc(
                eq(users.get(0).getId()),
                Mockito.any(Pageable.class))).thenReturn(pagedRes);

        when(itemRepository.findAllByRequest(requests.get(1).getId()))
                .thenReturn(Collections.singletonList(items.get(1)));

        List<ItemRequestDto>  itemRequests = itemRequestService.getItemRequestsFromOtherUsers(
                0,
                10,
                users.get(0).getId());

        assertEquals(requests.get(1).getId(), itemRequests.get(0).getId());
        assertEquals(requests.get(1).getDescription(), itemRequests.get(0).getDescription());
        assertEquals(requests.get(1).getRequester().getId(), itemRequests.get(0).getRequester());
        assertEquals(1, itemRequests.get(0).getItems().size());
        assertEquals(items.get(1).getId(), itemRequests.get(0).getItems().get(0).getId());
        assertEquals(items.get(1).getName(), itemRequests.get(0).getItems().get(0).getName());
        assertEquals(items.get(1).getOwner(), itemRequests.get(0).getItems().get(0).getOwner());
    }

    @Test
    void getItemRequestById() {
        when(itemRequestRepository.findById(requests.get(0).getId())).thenReturn(Optional.ofNullable(requests.get(0)));
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.empty());
        when(itemRepository.findAllByRequest(requests.get(0).getId()))
                .thenReturn(Collections.singletonList(items.get(0)));

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(requests.get(0).getId(), 10L));

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(10L, requests.get(0).getRequester().getId()));

        ItemRequestDto itemRequest = itemRequestService.getItemRequestById(
                requests.get(0).getId(),
                requests.get(0).getRequester().getId());

        assertEquals(requests.get(0).getId(), itemRequest.getId());
        assertEquals(requests.get(0).getDescription(), itemRequest.getDescription());
        assertEquals(requests.get(0).getRequester().getId(), itemRequest.getRequester());
        assertEquals(1, itemRequest.getItems().size());
        assertEquals(items.get(0).getId(), itemRequest.getItems().get(0).getId());
        assertEquals(items.get(0).getName(), itemRequest.getItems().get(0).getName());
        assertEquals(items.get(0).getOwner(), itemRequest.getItems().get(0).getOwner());
    }
}