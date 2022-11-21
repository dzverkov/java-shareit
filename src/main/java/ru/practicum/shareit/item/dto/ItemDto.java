package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<ItemComment> comments;

    @Data
    @NoArgsConstructor
    public static class ItemComment {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }

    @Data
    public static class Booking {
        private final long id;
        private final long bookerId;
    }
}
