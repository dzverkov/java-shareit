package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.exception.ApproveNotOwnerException;
import ru.practicum.shareit.booking.exception.BookerOrOwnerException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingWrongStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
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

        when(bookingRepository.findById(10L)).thenReturn(Optional.empty());
        when(bookingRepository.findById(bookings.get(0).getId())).thenReturn(Optional.of(bookings.get(0)));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(bookings.get(0));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(10L, true, users.get(0).getId()));

        assertThrows(ApproveNotOwnerException.class,
                () -> bookingService.approveBooking(bookings.get(0).getId(), true, users.get(1).getId()));

        bookings.get(0).setStatus(BookingStatus.APPROVED);
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(bookings.get(0).getId(), true, users.get(0).getId()));

        bookings.get(0).setStatus(BookingStatus.WAITING);
        BookingResultDto bookingResultDto = bookingService.approveBooking(
                bookings.get(0).getId(),
                true,
                users.get(0).getId());

        assertEquals(bookings.get(0).getId(), bookingResultDto.getId());
        assertEquals(bookings.get(0).getStart(), bookingResultDto.getStart());
        assertEquals(bookings.get(0).getEnd(), bookingResultDto.getEnd());
        assertEquals(bookings.get(0).getItem().getId(), bookingResultDto.getItem().getId());
        assertEquals(bookings.get(0).getBooker().getId(), bookingResultDto.getBooker().getId());
        assertEquals(bookings.get(0).getStatus(), bookingResultDto.getStatus());

    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(10L)).thenReturn(Optional.empty());
        when(bookingRepository.findById(bookings.get(0).getId())).thenReturn(Optional.of(bookings.get(0)));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(bookings.get(0));

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingById(bookings.get(0).getId(), 10L));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(10L, users.get(0).getId()));

        assertThrows(BookerOrOwnerException.class,
                () -> bookingService.getBookingById(bookings.get(0).getId(), users.get(1).getId()));

        BookingResultDto bookingResultDto = bookingService.getBookingById(bookings.get(0).getId(), users.get(0).getId());

        assertEquals(bookings.get(0).getId(), bookingResultDto.getId());
        assertEquals(bookings.get(0).getStart(), bookingResultDto.getStart());
        assertEquals(bookings.get(0).getEnd(), bookingResultDto.getEnd());
        assertEquals(bookings.get(0).getItem().getId(), bookingResultDto.getItem().getId());
        assertEquals(bookings.get(0).getBooker().getId(), bookingResultDto.getBooker().getId());
        assertEquals(bookings.get(0).getStatus(), bookingResultDto.getStatus());
    }

    @Test
    void getBookingsByUserId() {

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingsByUserId("ALL", 10L, 0, 10));

        assertThrows(BookingWrongStateException.class,
                () -> bookingService.getBookingsByUserId("UNKNOWN_STATE", 1L, 0, 10));

        PageImpl pagedRes = new PageImpl(Collections.singletonList(bookings.get(0)));

        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        List<BookingResultDto> bookingResultDtoRes = bookingService.getBookingsByUserId("ALL",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsByUserId("CURRENT",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsByUserId("PAST",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsByUserId("FUTURE",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsByUserId("WAITING",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsByUserId("REJECTED",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());
    }

    @Test
    void getBookingsItemsByUserId() {
        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingsItemsByUserId("ALL", 10L, 0, 10));

        assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingsItemsByUserId("UNKNOWN_STATE", 10L, 0, 10));

        PageImpl pagedRes = new PageImpl(Collections.singletonList(bookings.get(0)));

        when(bookingRepository.findAllByOwner_IdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        List<BookingResultDto> bookingResultDtoRes = bookingService.getBookingsItemsByUserId("ALL",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByOwner_IdCurrentByDateOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsItemsByUserId("CURRENT",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByOwner_IdAndEndBeforeOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsItemsByUserId("PAST",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByOwner_IdAndStartAfterOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsItemsByUserId("FUTURE",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByOwner_IdAndStatusOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsItemsByUserId("WAITING",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());

        when(bookingRepository.findAllByOwner_IdAndStatusOrderByStartDesc(
                Mockito.anyLong(),
                Mockito.any(BookingStatus.class),
                Mockito.any(Pageable.class)))
                .thenReturn(pagedRes);
        bookingResultDtoRes = bookingService.getBookingsItemsByUserId("REJECTED",
                1L, 0, 10);
        assertEquals(bookings.get(0).getId(), bookingResultDtoRes.get(0).getId());
    }
}