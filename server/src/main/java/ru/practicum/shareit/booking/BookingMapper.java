package ru.practicum.shareit.booking;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;

@Mapper
public interface BookingMapper {
    BookingResultDto toBookingDto(Booking booking);

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "booker.id", source = "bookerId")
    Booking toBooking(BookingParamDto bookingDto);
}
