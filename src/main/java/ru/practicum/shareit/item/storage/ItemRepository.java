package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item insert(Item item);

    Optional<Item> findById(long itemId);

    List<Item> findAllByUserId(long userId);

    List<Item> findAllByText(String text);

    void update(long itemId, Item updatedItem);

    void deleteById(long itemId);
}