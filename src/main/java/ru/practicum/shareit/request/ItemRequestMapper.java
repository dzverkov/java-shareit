package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestMapper {

    @Mapping(target = "requester.id", source = "requester")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "requester", source = "requester.id")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requestId", source = "request")
    ItemRequestDto.RequestItem toItemRequestDtoItem(Item item);
}
