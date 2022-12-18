package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingParamDto {

    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата окончания не может быть в прошлом")
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private BookingStatus status = BookingStatus.WAITING;
}

