package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Request 1 description",
            1L,
            LocalDateTime.now(),
            Collections.singletonList(new ItemRequestDto.RequestItem(
                    1L, "Item 1", "Item 1 description", true, 1L, 1L)
            ));


    @Test
    void addItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(Mockito.any(ItemRequestDto.class), eq(1L)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(itemRequestDto.getRequester()), Long.class));
    }

    @Test
    void getItemRequests() throws Exception {
        when(itemRequestService.getItemRequests(eq(1L)))
                .thenReturn(Collections.singletonList(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requester", is(itemRequestDto.getRequester()), Long.class))

                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].owner", is(itemRequestDto.getItems().get(0).getOwner()), Long.class))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getItemRequestsFromOtherUsers() throws Exception {
        when(itemRequestService.getItemRequestsFromOtherUsers(Mockito.anyInt(), Mockito.anyInt(), eq(1L)))
                .thenReturn(Collections.singletonList(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requester", is(itemRequestDto.getRequester()), Long.class))

                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].owner", is(itemRequestDto.getItems().get(0).getOwner()), Long.class))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(eq(1L), eq(1L)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(itemRequestDto.getRequester()), Long.class))

                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.items[0].owner", is(itemRequestDto.getItems().get(0).getOwner()), Long.class))
                .andExpect(jsonPath("$.items[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }
}