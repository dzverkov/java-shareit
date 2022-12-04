package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    private String description;
    @Column(name = "is_available")
    private boolean available;
    @Column(name = "owner_id", nullable = false)
    private Long owner;
    @Column(name = "request_id")
    private Long request;
}
