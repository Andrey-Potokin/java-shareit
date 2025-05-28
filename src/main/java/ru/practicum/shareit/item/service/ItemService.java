package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDto create(Long userId, NewItemRequest request);

    ItemDto findById(Long userId, Long itemId);

    @Transactional
    ItemDto update(Long userId, Long itemId, UpdateItemRequest request);

    List<ItemDto> findItemsByOwnerId(Long userId);

    List<ItemDto> findItemsByNameOrDescription(String text);

    @Transactional
    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}
