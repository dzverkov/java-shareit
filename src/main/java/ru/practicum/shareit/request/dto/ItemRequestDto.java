package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestItem {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long owner;
        private Long requestId;
    }

    private Long id;

    @NotBlank
    private String description;
    private Long requester;
    private LocalDateTime created = LocalDateTime.now();
    private List<RequestItem> items;
}
