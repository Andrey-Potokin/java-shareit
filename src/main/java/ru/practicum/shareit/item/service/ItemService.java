package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    ItemDto create(Long userId, NewItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    @Transactional
    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    List<ItemDto> findAllByOwnerId(Long userId);

    List<ItemDto> findByNameOrDescription(String text);

    @Transactional
    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}