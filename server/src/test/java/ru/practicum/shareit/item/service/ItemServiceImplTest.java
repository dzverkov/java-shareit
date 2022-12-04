package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUpdateException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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

class ItemServiceImplTest {
    private List<User> users;
    private List<Item> items;
    private List<Booking> bookings;

    private List<Comment> comments;

    private ItemRepository itemRepository;

    private ItemServiceImpl itemService;

    private BookingRepository bookingRepository;

    private CommentRepository commentRepository;

    @BeforeEach
    void init() {
        users = Arrays.asList(
                new User(1L, "User1", "user1@mail.ru"),
                new User(2L, "User2", "user2@mail.ru"),
                new User(3L, "User3", "user3@mail.ru")
        );

        items = Arrays.asList(
                new Item(1L, "Item1", "Item 1 description", true, 1L, null),
                new Item(2L, "Item2", "Item 2 description", true, 2L, null),
                new Item(3L, "Item3", "Item 3 description", true, 3L, 1L)
        );

        bookings = Arrays.asList(
                new Booking(1L, LocalDateTime.now().minusMonths(2), LocalDateTime.now().minusMonths(1),
                        items.get(0), users.get(2), BookingStatus.WAITING),
                new Booking(2L, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                        items.get(0), users.get(2), BookingStatus.WAITING)
        );

        comments = Arrays.asList(
                new Comment(1L, "Comment 1 for item 1", items.get(0), users.get(2), LocalDateTime.now())
        );

        itemRepository = mock(ItemRepository.class);
        when(itemRepository.findById(items.get(0).getId())).thenReturn(Optional.of(items.get(0)));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(users.get(1)));
        when(userRepository.findById(3L)).thenReturn(Optional.of(users.get(2)));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        bookingRepository = mock(BookingRepository.class);
        when(bookingRepository.findFirstByItem_IdAndItem_OwnerAndEndBeforeOrderByEndDesc(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(bookings.get(0)));
        when(bookingRepository.findFirstByItem_IdAndItem_OwnerAndEndAfterOrderByEnd(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(bookings.get(1)));

        commentRepository = mock(CommentRepository.class);
        when(commentRepository.findAllByItem_Id(items.get(0).getId()))
                .thenReturn(comments);


        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository);
    }

    @Test
    void addItem() {
        Item item1 = new Item(1L, "NewItem1", "New Item 1 description",
                true, users.get(0).getId(), null);

        when(itemRepository.save(item1)).thenReturn(item1);

        ItemDto itemDto = new ItemDto(1L, item1.getName(), item1.getDescription(), item1.isAvailable(),
                null, null, null, null);

        itemDto.setName(item1.getName());
        itemDto.setDescription(item1.getDescription());
        itemDto.setAvailable(item1.isAvailable());

        ItemDto itemDtoRes = itemService.addItem(itemDto, 1L);

        assertEquals(item1.getId(), itemDtoRes.getId());
        assertEquals(item1.getName(), itemDtoRes.getName());
        assertEquals(item1.getDescription(), itemDtoRes.getDescription());
        assertEquals(item1.isAvailable(), itemDtoRes.getAvailable());
    }

    @Test
    void updateItem() {
        Item item1 = new Item(1L, "UpdItem1", "Updated Item 1 description",
                true, users.get(0).getId(), null);
        when(itemRepository.save(item1)).thenReturn(item1);

        ItemDto itemDto = new ItemDto(1L, item1.getName(), item1.getDescription(), item1.isAvailable(),
                null, null, null, null);

        assertThrows(ItemUpdateException.class,
                () -> itemService.updateItem(itemDto, 1L, 3L));

        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, 10L, 1L));

        ItemDto itemDtoRes = itemService.updateItem(itemDto, 1L, 1L);

        assertEquals(item1.getId(), itemDtoRes.getId());
        assertEquals(item1.getName(), itemDtoRes.getName());
        assertEquals(item1.getDescription(), itemDtoRes.getDescription());
        assertEquals(item1.isAvailable(), itemDtoRes.getAvailable());
    }

    @Test
    void getItemById() {

        assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(10L, users.get(0).getId()));

        ItemDto itemDtoRes = itemService.getItemById(items.get(0).getId(), users.get(0).getId());
        Item item1 = items.get(0);

        assertEquals(item1.getId(), itemDtoRes.getId());
        assertEquals(item1.getName(), itemDtoRes.getName());
        assertEquals(item1.getDescription(), itemDtoRes.getDescription());
        assertEquals(item1.isAvailable(), itemDtoRes.getAvailable());

        assertEquals(bookings.get(0).getId(), itemDtoRes.getLastBooking().getId());
        assertEquals(bookings.get(0).getBooker().getId(), itemDtoRes.getLastBooking().getBookerId());

        assertEquals(bookings.get(1).getId(), itemDtoRes.getNextBooking().getId());
        assertEquals(bookings.get(1).getBooker().getId(), itemDtoRes.getNextBooking().getBookerId());

        assertEquals(comments.get(0).getId(), itemDtoRes.getComments().get(0).getId());
        assertEquals(comments.get(0).getText(), itemDtoRes.getComments().get(0).getText());
        assertEquals(comments.get(0).getAuthor().getName(), itemDtoRes.getComments().get(0).getAuthorName());
    }

    @Test
    void getItemsByUserId() {

        Page<Item> pagedRes = new PageImpl(Collections.singletonList(items.get(0)));
        when(itemRepository.findAllByOwner(eq(users.get(0).getId()), Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);

        List<ItemDto> listRes = itemService.getItemsByUserId(users.get(0).getId(), 0, 10);

        Item item1 = items.get(0);

        assertEquals(1, listRes.size());

        assertEquals(item1.getId(), listRes.get(0).getId());
        assertEquals(item1.getName(), listRes.get(0).getName());
        assertEquals(item1.getDescription(), listRes.get(0).getDescription());
        assertEquals(item1.isAvailable(), listRes.get(0).getAvailable());

        assertEquals(bookings.get(0).getId(), listRes.get(0).getLastBooking().getId());
        assertEquals(bookings.get(0).getBooker().getId(), listRes.get(0).getLastBooking().getBookerId());

        assertEquals(bookings.get(1).getId(), listRes.get(0).getNextBooking().getId());
        assertEquals(bookings.get(1).getBooker().getId(), listRes.get(0).getNextBooking().getBookerId());

        assertEquals(comments.get(0).getId(), listRes.get(0).getComments().get(0).getId());
        assertEquals(comments.get(0).getText(), listRes.get(0).getComments().get(0).getText());
        assertEquals(comments.get(0).getAuthor().getName(), listRes.get(0).getComments().get(0).getAuthorName());

    }

    @Test
    void searchItems() {
        Page<Item> pagedRes = new PageImpl(Collections.singletonList(items.get(0)));
        when(itemRepository.searchItems(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);

        List<ItemDto> listRes = itemService.searchItems("Item", 0, 10);

        Item item1 = items.get(0);

        assertEquals(1, listRes.size());

        assertEquals(item1.getId(), listRes.get(0).getId());
        assertEquals(item1.getName(), listRes.get(0).getName());
        assertEquals(item1.getDescription(), listRes.get(0).getDescription());
        assertEquals(item1.isAvailable(), listRes.get(0).getAvailable());

    }

    @Test
    void addComment() {

        CommentDto commentDto = new CommentDto(
                comments.get(0).getId(),
                comments.get(0).getText(),
                comments.get(0).getItem().getId(),
                comments.get(0).getAuthor().getId(),
                comments.get(0).getAuthor().getName(),
                LocalDateTime.of(2022, 11, 1, 12, 0)
        );

        Comment comment1 = comments.get(0);

        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment1);

        assertThrows(UserNotFoundException.class,
                () -> itemService.addComment(commentDto, comments.get(0).getItem().getId(),
                        10L));
        assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(commentDto, 10L,
                        comments.get(0).getAuthor().getId()));

        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(
                eq(2L),
                eq(items.get(0).getId()),
                Mockito.any(LocalDateTime.class))).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(
                eq(users.get(2).getId()),
                eq(items.get(0).getId()),
                Mockito.any(LocalDateTime.class))).thenReturn(Optional.of(bookings.get(0)));

        assertThrows(ValidationException.class,
                () -> itemService.addComment(commentDto, comments.get(0).getItem().getId(),
                        2L));

        CommentDto commentDtoRes = itemService.addComment(commentDto, comments.get(0).getItem().getId(),
                comments.get(0).getAuthor().getId());

        assertEquals(commentDto.getId(), commentDtoRes.getId());
        assertEquals(commentDto.getText(), commentDtoRes.getText());
        assertEquals(commentDto.getAuthorId(), commentDtoRes.getAuthorId());
        assertEquals(commentDto.getAuthorName(), commentDtoRes.getAuthorName());
    }
}