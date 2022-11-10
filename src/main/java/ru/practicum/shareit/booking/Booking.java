package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;

    @FutureOrPresent
    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    private Item item;
    @ManyToOne
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
