package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUpdateException;
import ru.practicum.shareit.item.model.Comment;
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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {

        User user = getUser(userId);
        validateItem(itemDto);
        Item item = mapper.toItem(itemDto);
        item.setOwner(user.getId());
        return mapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {

        User user = getUser(userId);
        Optional<Item> existItemOpt = itemRepository.findById(itemId);

        if (existItemOpt.isPresent()) {
            Item existItem = existItemOpt.get();
            if (!user.getId().equals(existItem.getOwner())) {
                throw new ItemUpdateException(
                        String.format("Пользователь с id = %d не является владельцем вещи с id = %d", userId, itemId)
                );
            }

            mergeFields(itemDto, existItem);

            validateItem(mapper.toItemDto(existItem));
            return mapper.toItemDto(itemRepository.save(existItem));
        } else {
            throw new ItemNotFoundException(String.format("Вещь с id = %d не найдена.", itemId));
        }
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id = %d не найдена.", itemId)));

        ItemDto itemDto = mapper.toItemDto(item);
        itemDto.setLastBooking(getLastBooking(itemId, userId));
        itemDto.setNextBooking(getNextBooking(itemId, userId));
        itemDto.setComments(getComments(itemId));

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size) {
        User user = getUser(userId);

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Item> items = itemRepository.findAllByOwner(user.getId(), pageRequest);
        return items.stream().map(item -> {
            ItemDto itemDto = mapper.toItemDto(item);
            itemDto.setLastBooking(getLastBooking(item.getId(), userId));
            itemDto.setNextBooking(getNextBooking(item.getId(), userId));
            itemDto.setComments(getComments(item.getId()));
            return itemDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Item> items = itemRepository.searchItems(text, pageRequest);
        return items.stream().map(mapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {

        // Проверка наличия пользователя
        User author = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь userId = %d не найден.", userId))
        );
        // Проверка наличия вещи
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format("Вещь itemId = %d не найден.", itemId)));

        // Проверка, что комментарий оставляет арендатор вещи
        bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(
                userId,
                itemId,
                LocalDateTime.now()).orElseThrow(
                () -> new ValidationException("Комментарии может оставлять только арендатор вещи.")
        );

        Comment comment = mapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return mapper.toCommentDto(commentRepository.save(comment));
    }

    private void validateItem(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Значение name не задано или пустое.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Значение description не задано или пустое.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Значение available = null.");
        }
    }

    private User getUser(Long userId) {
        if (userId == null || userId < 0) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return user.get();
    }

    private void mergeFields(ItemDto source, Item target) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getAvailable() != null) target.setAvailable(source.getAvailable());
    }

    private ItemDto.Booking getLastBooking(Long itemId, Long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findFirstByItem_IdAndItem_OwnerAndEndBeforeOrderByEndDesc(
                itemId,
                userId,
                LocalDateTime.now());
        if (bookingOpt.isEmpty()) {
            return null;
        } else {
            return new ItemDto.Booking(bookingOpt.get().getId(), bookingOpt.get().getBooker().getId());
        }
    }

    private ItemDto.Booking getNextBooking(Long itemId, Long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findFirstByItem_IdAndItem_OwnerAndEndAfterOrderByEnd(
                itemId,
                userId,
                LocalDateTime.now());
        if (bookingOpt.isEmpty()) {
            return null;
        } else {
            return new ItemDto.Booking(bookingOpt.get().getId(), bookingOpt.get().getBooker().getId());
        }
    }

    private List<ItemDto.ItemComment> getComments(Long itemId) {

        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);

        return comments.stream().map(mapper::toItemDtoComment).collect(Collectors.toList());
    }
}
