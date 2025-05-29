package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemRequest request);

    ItemDto findById(Long userId, Long itemId);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest request);

    List<ItemDto> findItemsByOwnerId(Long userId);

    List<ItemDto> findItemsByNameOrDescription(String text);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}