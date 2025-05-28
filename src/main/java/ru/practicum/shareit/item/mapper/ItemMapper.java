package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item mapToItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static Item mapToNewItem(NewItemDto requestItemDto) {
        return Item.builder()
                .name(requestItemDto.getName())
                .description(requestItemDto.getDescription())
                .available(requestItemDto.getAvailable())
                .build();
    }

    public static void updateItemFields(Item item, UpdateItemDto requestItemDto) {
        if (requestItemDto.hasName()) {
            item.setName(requestItemDto.getName());
        }
        if (requestItemDto.hasDescription()) {
            item.setDescription(requestItemDto.getDescription());

        }
        if (requestItemDto.hasAvailable()) {
            item.setAvailable(requestItemDto.getAvailable());
        }
    }
}