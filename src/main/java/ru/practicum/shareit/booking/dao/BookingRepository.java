package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
            Long userId,
            LocalDateTime dateStart,
            LocalDateTime dateEnd,
            Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            " where exists (select 0 from Item i where b.item.id = i.id and i.owner = ?1)" +
            " order by b.start desc")
    Page<Booking> findAllByOwner_IdOrderByStartDesc(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            " where exists (select 0 from Item i where b.item.id = i.id and i.owner = ?1)" +
            " and ?2 between b.start and b.end" +
            " order by b.start desc")
    Page<Booking> findAllByOwner_IdCurrentByDateOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b " +
            " where exists (select 0 from Item i where b.item.id = i.id and i.owner = ?1)" +
            " and b.end < ?2" +
            " order by b.start desc")
    Page<Booking> findAllByOwner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b " +
            " where exists (select 0 from Item i where b.item.id = i.id and i.owner = ?1)" +
            " and b.start > ?2" +
            " order by b.start desc")
    Page<Booking> findAllByOwner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b " +
            " where exists (select 0 from Item i where b.item.id = i.id and i.owner = ?1)" +
            " and b.status = ?2" +
            " order by b.start desc")
    Page<Booking> findAllByOwner_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndItem_OwnerAndEndBeforeOrderByEndDesc(Long itemId, Long userId, LocalDateTime date);

    Optional<Booking> findFirstByItem_IdAndItem_OwnerAndEndAfterOrderByEnd(Long itemId, Long userId, LocalDateTime date);

    Optional<Booking> findFirstByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime date);

}
