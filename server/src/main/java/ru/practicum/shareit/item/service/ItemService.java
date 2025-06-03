package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.NewItemRequest;
import ru.practicum.shareit.item.dto.item.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemRequest request);

    ItemDto findById(Long userId, Long itemId);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest request);

    List<ItemDto> findItemsByOwnerId(Long userId);

    List<ItemDto> findItemsByNameOrDescription(String text);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}