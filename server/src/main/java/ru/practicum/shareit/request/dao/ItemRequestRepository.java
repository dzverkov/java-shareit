package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester_IdOrderByCreatedDesc(Long requesterId);

    Page<ItemRequest> findAllByRequester_IdIsNot(Long requesterId, Pageable pageable);
}
