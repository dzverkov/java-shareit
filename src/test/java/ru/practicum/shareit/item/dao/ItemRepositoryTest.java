package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    public void verifyRepositoryByPersistingAnItem() {

        User user = new User(1L, "User1", "user1@mail.ru");
        userRepository.save(user);

        Item item1 = new Item(null,
                "Item1",
                "Item 1 description",
                true,
                1L,
                null);

        assertNull(item1.getId());
        itemRepository.save(item1);
        assertNotNull(item1.getId());
    }

    @Test
    void searchItems() {
        User user = new User(1L, "User1", "user1@mail.ru");
        userRepository.save(user);

        Item item1 = new Item(null, "Item1", "Item 1 description", true, 1L, null);
        Item item2 = new Item(null, "Item2", "Item 2 description", true, 1L, null);

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> res = itemRepository.searchItems("ITem", Pageable.unpaged()).get().collect(Collectors.toList());

        assertEquals(2, res.size());
    }
}