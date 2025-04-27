package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepositoryImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepositoryImpl userRepository;

    @Override
    public ItemDto createItem(Long userId, NewItemRequest request) {
        if (userRepository.findById(userId).isPresent()) {
            Item item = ItemMapper.mapToItem(request);
            item.setOwnerId(userId);
            itemRepository.insert(item);
            return ItemMapper.mapToItemDto(item);
        } else {
            log.error("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        if (itemRepository.findAllByUserId(userId).isEmpty()) {
            log.error("Пользователь с id {} не имеет вещей", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не имеет вещей");
        } else {
            return itemRepository.findAllByUserId(userId)
                    .stream()
                    .map(ItemMapper::mapToItemDto)
                    .toList();
        }
    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        if (itemRepository.findAllByText(text).isEmpty()) {
            log.error("Вещи с текстом {} не найдены", text);
            throw new NotFoundException("Вещи с текстом " + text + " не найдены");
        } else {
            return itemRepository.findAllByText(text).stream()
                    .filter(item -> item.getAvailable() == true)
                    .map(ItemMapper::mapToItemDto)
                    .toList();
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, UpdateItemRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        validateOwner(userId, item);
        Item updatedItem = ItemMapper.updateItemFields(item, request);
        itemRepository.update(itemId, updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        validateOwner(userId, item);
        itemRepository.deleteById(itemId);
    }

    private void validateOwner(Long userId, Item item) {
        if (item.getOwnerId() != userId) {
            log.error("Пользователь с id " + userId + " не является владельцем вещи с id " + item.getId());
            throw new NotOwnerException("Пользователь с id " + userId + " не является владельцем вещи с id "
                    + item.getId());
        }
    }
}