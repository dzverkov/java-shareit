package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    private List<User> users;
    private List<Item> items;
    private List<Booking> bookings;

    private ItemRepository itemRepository;

    private BookingServiceImpl bookingService;

    private BookingRepository bookingRepository;

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

        itemRepository = mock(ItemRepository.class);
        when(itemRepository.findById(items.get(0).getId())).thenReturn(Optional.of(items.get(0)));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(users.get(1)));
        when(userRepository.findById(3L)).thenReturn(Optional.of(users.get(2)));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        bookingRepository = mock(BookingRepository.class);
        /*when(bookingRepository.findFirstByItem_IdAndItem_OwnerAndEndBeforeOrderByEndDesc(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(bookings.get(0)));
        when(bookingRepository.findFirstByItem_IdAndItem_OwnerAndEndAfterOrderByEnd(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(bookings.get(1)));
*/

        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
    }

    @Test
    void addBooking() {

        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(bookings.get(0));

        BookingParamDto bookingDto = new BookingParamDto(
              bookings.get(0).getStart(),
              bookings.get(0).getEnd(),
              bookings.get(0).getItem().getId(),
              bookings.get(0).getBooker().getId(),
              bookings.get(0).getStatus()
        );

        assertThrows(UserNotFoundException.class,
                () -> bookingService.addBooking(bookingDto, 10L));

        bookingDto.setItemId(10L);
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.addBooking(bookingDto, users.get(2).getId()));

        bookingDto.setItemId(bookings.get(0).getItem().getId());
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.addBooking(bookingDto, users.get(0).getId()));

        items.get(0).setAvailable(false);
        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDto, users.get(2).getId()));

        items.get(0).setAvailable(true);
        LocalDateTime tmpDt = bookingDto.getStart();
        bookingDto.setStart(bookingDto.getEnd());
        bookingDto.setEnd(tmpDt);
        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingDto, users.get(2).getId()));

        tmpDt = bookingDto.getStart();
        bookingDto.setStart(bookingDto.getEnd());
        bookingDto.setEnd(tmpDt);

        BookingResultDto bookingResultDto = bookingService.addBooking(bookingDto, users.get(2).getId());

        assertEquals(bookings.get(0).getId(), bookingResultDto.getId());
        assertEquals(bookingDto.getStart(), bookingResultDto.getStart());
        assertEquals(bookingDto.getEnd(), bookingResultDto.getEnd());
        assertEquals(bookingDto.getItemId(), bookingResultDto.getItem().getId());
        assertEquals(bookingDto.getBookerId(), bookingResultDto.getBooker().getId());
        assertEquals(bookingDto.getStatus(), bookingResultDto.getStatus());
    }

    @Test
    void approveBooking() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingsByUserId() {
    }

    @Test
    void getBookingsItemsByUserId() {
    }
}