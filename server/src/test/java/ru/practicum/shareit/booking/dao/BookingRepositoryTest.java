package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private Booking booking;


    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void verifyRepositoryByPersistingBooking() {

        User user = new User(1L, "User1", "user1@mail.ru");
        userRepository.save(user);

        Item item1 = new Item(null,
                "Item1",
                "Item 1 description",
                true,
                1L,
                null);
        itemRepository.save(item1);

        Booking booking = new Booking(null, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(2),
                item1, user, BookingStatus.WAITING);

        assertNull(booking.getId());
        bookingRepository.save(booking);
        assertNotNull(booking.getId());
    }

    @Test
    void findAllByOwner_IdOrderByStartDesc() {
        prepareData();

        List<Booking> res = bookingRepository.findAllByOwner_Id(1L, Pageable.unpaged())
                .get().collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
        assertEquals(user, res.get(0).getBooker());
        assertEquals(item, res.get(0).getItem());
        assertEquals(booking.getStatus(), res.get(0).getStatus());
    }

    @Test
    void findAllByOwner_IdCurrentByDate() {
        prepareData();

        List<Booking> res = bookingRepository.findAllByOwner_IdCurrentByDate(1L,
                        LocalDateTime.now().plusMonths(2),
                        Pageable.unpaged())
                .get().collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
        assertEquals(user, res.get(0).getBooker());
        assertEquals(item, res.get(0).getItem());
        assertEquals(booking.getStatus(), res.get(0).getStatus());
    }

    @Test
    void findAllByOwner_IdAndEndBefore() {
        prepareData();

        List<Booking> res = bookingRepository.findAllByOwner_IdAndEndBefore(1L,
                        LocalDateTime.now().plusMonths(4),
                        Pageable.unpaged())
                .get().collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
        assertEquals(user, res.get(0).getBooker());
        assertEquals(item, res.get(0).getItem());
        assertEquals(booking.getStatus(), res.get(0).getStatus());
    }

    @Test
    void findAllByOwner_IdAndStartAfter() {
        prepareData();

        List<Booking> res = bookingRepository.findAllByOwner_IdAndStartAfter(1L,
                        LocalDateTime.now(),
                        Pageable.unpaged())
                .get().collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
        assertEquals(user, res.get(0).getBooker());
        assertEquals(item, res.get(0).getItem());
        assertEquals(booking.getStatus(), res.get(0).getStatus());
    }

    @Test
    void findAllByOwner_IdAndStatus() {
        prepareData();

        List<Booking> res = bookingRepository.findAllByOwner_IdAndStatus(1L,
                        BookingStatus.WAITING,
                        Pageable.unpaged())
                .get().collect(Collectors.toList());

        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
        assertEquals(user, res.get(0).getBooker());
        assertEquals(item, res.get(0).getItem());
        assertEquals(booking.getStatus(), res.get(0).getStatus());
    }

    private void prepareData() {
        user = new User(1L, "User1", "user1@mail.ru");
        userRepository.save(user);

        item = new Item(null,
                "Item1",
                "Item 1 description",
                true,
                user.getId(),
                null);
        itemRepository.save(item);

        booking = new Booking(null, LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusMonths(3),
                item, user, BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

}