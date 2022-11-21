package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    @Mapping(target = "requestId", source = "request")
    ItemDto toItemDto(Item item);

    @Mapping(target = "request", source = "requestId")
    Item toItem(ItemDto item);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "author.name", source = "authorName")
    Comment toComment(CommentDto comment);

    @Mapping(target = "authorName", source = "author.name")
    ItemDto.ItemComment toItemDtoComment(Comment comment);

}
