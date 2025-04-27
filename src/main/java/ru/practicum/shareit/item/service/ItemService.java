package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, NewItemRequest request);

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItems(long userId);

    List<ItemDto> getAllByText(String text);

    ItemDto updateItem(long userId, long itemId, UpdateItemRequest request);

    void deleteItem(long userId, long itemId);
}