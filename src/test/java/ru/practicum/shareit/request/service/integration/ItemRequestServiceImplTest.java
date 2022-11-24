package ru.practicum.shareit.request.service.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void getItemRequests() {

        User user = new User(1L, "User1", "user1@mail.ru");
        User requester = new User(2L, "User2", "user2@mail.ru");

        Item item = new Item(1L, "Item1", "Item 1 description",
                true, user.getId(), 1L);

        ItemRequest itemRequest = new ItemRequest(1L, "Request description",
                requester, LocalDateTime.now());

        userRepository.save(user);
        userRepository.save(requester);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);

        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequests(requester.getId());

        assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(itemRequest.getRequester().getId(), itemRequests.get(0).getRequester());
        assertEquals(1, itemRequests.get(0).getItems().size());
        assertEquals(item.getName(), itemRequests.get(0).getItems().get(0).getName());
        assertEquals(item.getDescription(), itemRequests.get(0).getItems().get(0).getDescription());
    }
}