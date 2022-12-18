package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final ItemDto itemDto = new ItemDto(
            1L, "Item 1", "Item 1 description", true, 1L,
            new ItemDto.Booking(1L, 1L),
            new ItemDto.Booking(2L, 1L),
            Collections.singletonList(
                    new ItemDto.ItemComment(1L, "Comment 1 for item 1", "User 1", LocalDateTime.now())
            ));

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(Mockito.any(ItemDto.class), eq(1L)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(Mockito.any(ItemDto.class), eq(1L), eq(1L)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(eq(1L), eq(1L)))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)))
        ;
    }

    @Test
    void getItemsByUserId() throws Exception {
        when(itemService.getItemsByUserId(1L, 0, 10))
                .thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(itemDto))));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(itemDto))));
    }

    @Test
    void addComment() throws Exception {

        CommentDto commentDto = new CommentDto(1L, "Comment 1 for item 1", 1L, 1L,
                "User 1", LocalDateTime.now());

        when(itemService.addComment(Mockito.any(CommentDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
    }
}