package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.NewItemRequest;
import ru.practicum.shareit.item.dto.item.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody NewItemRequest request) {
        log.debug("Принят запрос на добавление вещи пользователем с ID={} и запросом: {}", userId, request);
        return itemService.create(userId, request);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                       @PathVariable(name = "itemId") long itemId) {
        log.debug("Принят запрос на получение вещи с ID={}", itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Принят запрос на получение всех вещей пользователя с ID={}", userId);
        return itemService.findItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByText(@RequestParam(name = "text") String text) {
        log.debug("Принят запрос на получение всех вещей по тексту {}", text);
        if (text.isBlank()) {
            log.warn("Поиск вещей по пустому тексту");
            return List.of();
        } else {
            return itemService.findItemsByNameOrDescription(text);
        }
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@PathVariable(name = "itemId") long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody UpdateItemRequest request) {
        log.debug("Принят запрос на обновление вещи с ID={} пользователем с ID={} и запросом: {}", itemId, userId, request);
        return itemService.update(userId, itemId, request);
    }

    @PostMapping("{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @RequestBody NewCommentDto commentDto) {
        log.debug("Принят запрос на добавление комментария к вещи с ID={} пользователем с ID={} и запросом: {}", itemId, userId,
                commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }
}