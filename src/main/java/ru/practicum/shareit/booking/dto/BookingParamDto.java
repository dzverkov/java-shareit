package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    @Data
    public static class UserDto{
        private Long id;
    }

    @Data
    public static class ItemDto{
        private Long id;
        private String name;
    }

    private Long id;

    @FutureOrPresent
    private LocalDate start;
    @Future
    private LocalDate end;

    private Long itemId;

    //private Long bookerId;

    private ItemDto item;
    private UserDto booker;

    private BookingStatus status = BookingStatus.WAITING;

}

