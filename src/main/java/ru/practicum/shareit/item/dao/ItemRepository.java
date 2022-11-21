package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwner(Long userId, Pageable pageable);

    @Query(" select i from Item i " +
            "where " +
            "?1 is not null and length(?1) > 0" +
            "and i.available = true " +
            "and (" +
            "lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%')))")
    Page<Item> searchItems(String text, Pageable pageable);

    List<Item> findAllByRequest(Long requestId);

}
