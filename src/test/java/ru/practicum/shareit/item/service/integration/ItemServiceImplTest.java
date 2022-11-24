package ru.practicum.shareit.item.service.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getItemsByUserId() {

        User user = new User(1L, "User1", "user1@mail.ru");
        User booker = new User(2L, "User2", "user2@mail.ru");

        Item item = new Item(1L, "Item1", "Item 1 description",
                true, user.getId(), null);

        Booking bookingPrev  = new Booking(1L, LocalDateTime.now().minusMonths(2), LocalDateTime.now().minusMonths(1),
                        item, booker, BookingStatus.WAITING);

        Booking bookingNext  = new Booking(2L, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                        item, booker, BookingStatus.WAITING);

        Comment comment = new Comment(1L, "Comment 1 for item 1", item, booker, LocalDateTime.now());

        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(bookingPrev);
        bookingRepository.save(bookingNext);
        commentRepository.save(comment);

        List<ItemDto> listRes = itemService.getItemsByUserId(1L, 0, 10);

        assertEquals(1, listRes.size());

        assertEquals(item.getId(), listRes.get(0).getId());
        assertEquals(item.getName(), listRes.get(0).getName());
        assertEquals(item.getDescription(), listRes.get(0).getDescription());
        assertEquals(item.isAvailable(), listRes.get(0).getAvailable());

        assertEquals(bookingPrev.getId(), listRes.get(0).getLastBooking().getId());
        assertEquals(bookingPrev.getBooker().getId(), listRes.get(0).getLastBooking().getBookerId());

        assertEquals(bookingNext.getId(), listRes.get(0).getNextBooking().getId());
        assertEquals(bookingNext.getBooker().getId(), listRes.get(0).getNextBooking().getBookerId());

        assertEquals(comment.getId(), listRes.get(0).getComments().get(0).getId());
        assertEquals(comment.getText(), listRes.get(0).getComments().get(0).getText());
        assertEquals(comment.getAuthor().getName(), listRes.get(0).getComments().get(0).getAuthorName());
    }
}