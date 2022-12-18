package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResultDtoTest {

    @Autowired
    private JacksonTester<BookingResultDto> json;

    @Test
    void bookingResultDtoJsonTest() throws IOException {

        BookingResultDto bookingDto = new BookingResultDto(1L,
                LocalDateTime.of(2032, 12, 1, 12, 0),
                LocalDateTime.of(2032, 12, 30, 12, 0),
                new BookingResultDto.ItemDto(1L, "Item1"),
                new BookingResultDto.UserDto(1L),
                BookingStatus.WAITING);

        JsonContent<BookingResultDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2032-12-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2032-12-30T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item1");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}