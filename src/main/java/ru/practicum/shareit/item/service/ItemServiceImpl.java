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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, NewItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });
        Item item = itemMapper.toItem(request);
        item.setOwner(user);
        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
            log.error("Пользователь с id {} не имеет вещей", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не имеет вещей");
        } else {
            return itemRepository.findAllByOwnerId(userId)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .toList();
        }
    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        if (itemRepository.findAllByNameContainingIgnoreCase(text).isEmpty()) {
            log.error("Вещи с текстом {} не найдены", text);
            throw new NotFoundException("Вещи с текстом " + text + " не найдены");
        } else {
            return itemRepository.findAllByNameContainingIgnoreCase(text).stream()
                    .filter(Item::getAvailable)
                    .map(itemMapper::toItemDto)
                    .toList();
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, UpdateItemRequest request) {
        Item updatedItem = itemRepository.findById(itemId)
                .map(item -> {
                    validateOwner(userId, item);
                    return updateItemFields(item, request);
                })
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        itemRepository.save(updatedItem);
        return itemMapper.toItemDto(updatedItem);
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

    private Item validateOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователь с id " + userId + " не является владельцем вещи с id " + item.getId());
            throw new NotOwnerException("Пользователь с id " + userId + " не является владельцем вещи с id "
                    + item.getId());
        }
        return item;
    }

    private Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }
}