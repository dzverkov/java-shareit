package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingSearchState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.exception.ApproveNotOwnerException;
import ru.practicum.shareit.booking.exception.BookerOrOwnerException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Override
    public BookingResultDto addBooking(BookingParamDto bookingDto, Long userId) {

        // Проверка наличия пользователя
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь userId = %d не найден.", userId))
        );
        // Проверка наличия вещи
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException(String.format("Вещь itemId = %d не найден.", bookingDto.getItemId())));

        if (item.getOwner().equals(userId)) {
            throw new ItemNotFoundException("Владелец не может забронировать свою вещь");
        }

        if (!item.isAvailable()) {
            throw new ValidationException(String.format("Вещь itemId = %d не доступна для бронирования.", item.getId()));
        }

        bookingDto.setBookerId(userId);
        Booking booking = mapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResultDto approveBooking(Long bookingId, Boolean approved, Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException(
                        String.format("Заявка от пользователя userId = %d с bookingId = %d не найдена",
                                userId, bookingId))
        );

        if (!booking.getItem().getOwner().equals(userId)) {
            throw new ApproveNotOwnerException(String.format("Пользователь userId = %d не является владельцем вещи " +
                    "по заявке bookingId = %d", userId, bookingId));
        }

        BookingStatus bookingStatus = (approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        if (booking.getStatus().equals(bookingStatus)) {
            throw new ValidationException("Заявка уже одобрена");
        }

        booking.setStatus(bookingStatus);
        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResultDto getBookingById(Long bookingId, Long userId) {

        // Проверка наличия пользователя
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь userId = %d не найден.", userId))
        );

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new BookingNotFoundException(String.format("Заявка от пользователя userId = %d с bookingId = %d",
                    userId, bookingId));
        }

        Booking booking = bookingOpt.get();

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new BookerOrOwnerException(
                    String.format("Пользователь userId = %d должен являться либо автором заявки или владельцем вещи.",
                            userId)
            );
        }

        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingResultDto> getBookingsByUserId(String state, Long userId, Integer from, Integer size) {

        // Проверка наличия пользователя
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь userId = %d не найден.", userId))
        );

        BookingSearchState.valueOf(state);

        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(page, size, sort);

        switch (BookingSearchState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBooker_Id(userId, pageable)
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                LocalDateTime currentDate = LocalDateTime.now();
                return bookingRepository.findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqual(
                                userId,
                                currentDate,
                                currentDate,
                                pageable
                        ).stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndBefore(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartAfter(
                                userId,
                                LocalDateTime.now(),
                                pageable)
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatus(
                                userId,
                                BookingStatus.WAITING,
                                pageable
                        )
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatus(
                                userId,
                                BookingStatus.REJECTED,
                                pageable)
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return List.of();
        }
    }

    @Override
    public List<BookingResultDto> getBookingsItemsByUserId(String state, Long userId, Integer from, Integer size) {
        // Проверка наличия пользователя
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь userId = %d не найден.", userId))
        );

        BookingSearchState.valueOf(state);

        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        switch (BookingSearchState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByOwner_Id(userId, pageRequest)
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByOwner_IdCurrentByDate(
                                userId,
                                LocalDateTime.now(),
                                pageRequest
                        )
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByOwner_IdAndEndBefore(
                                userId,
                                LocalDateTime.now(),
                                pageRequest
                        )
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByOwner_IdAndStartAfter(
                                userId,
                                LocalDateTime.now(),
                                pageRequest
                        )
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByOwner_IdAndStatus(
                                userId,
                                BookingStatus.WAITING,
                                pageRequest
                        )
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByOwner_IdAndStatus(
                                userId,
                                BookingStatus.REJECTED,
                                pageRequest
                        )
                        .stream()
                        .map(mapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return List.of();
        }
    }
}
