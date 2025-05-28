package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(Long ownerId, NewItemRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", ownerId);
                    return new NotFoundException("Пользователь с id " + ownerId + " не найден");
                });
        Item item = ItemMapper.toItem(request);
        item.setOwner(owner);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        if (itemRepository.findAllByOwnerId(ownerId).isEmpty()) {
            log.error("Пользователь с id {} не имеет вещей", ownerId);
            throw new NotFoundException("Пользователь с id " + ownerId + " не имеет вещей");
        } else {
            List<ItemDto> itemDtos = itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                    .map(ItemMapper::toItemDto)
                    .toList();
            log.debug("Список вещей {}", itemDtos);
            itemDtos.forEach(this::loadDetails);
            log.debug("Список вещей {}", itemDtos);

            return itemDtos;
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
                    .map(ItemMapper::toItemDto)
                    .toList();
        }
    }

    @Override
    public ItemDto updateItem(long ownerId, long itemId, UpdateItemRequest request) {
        Item updatedItem = itemRepository.findById(itemId)
                .map(item -> {
                    validateOwner(ownerId, item);
                    return updateItemFields(item, request);
                })
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public void deleteItem(long ownerId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        validateOwner(ownerId, item);
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, NewCommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", authorId);
                    return new NotFoundException("Пользователь с id " + authorId + " не найден");
                });
        validateCommentAuthorAndDate(authorId, itemId);

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
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

    private void validateOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователь с id {} не является владельцем вещи с id {}", userId, item.getId());
            throw new NotOwnerException("Пользователь с id " + userId + " не является владельцем вещи с id "
                    + item.getId());
        }
    }

    private void validateCommentAuthorAndDate(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);

        if (bookings.isEmpty()) {
            throw new ValidationException("Для данной вещи ещё не было бронирований! Вы не можете оставить комментарий!");
        }

        Booking booking = bookings.stream()
                .filter(booking1 -> booking1.getBooker().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("Пользователь не имеет права оставлять комментарий, " +
                        "так как не был арендатором!"));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new ForbiddenException("Пользователь не имеет подтвержденного бронирования!");
        }

        if (!booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Пользователь не имеет завершенного бронирования!");
        }
    }

    private void loadDetails(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(itemDto.getId(),
                Sort.by(Sort.Direction.DESC, "start"));

        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.get(0);
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));

            if (bookings.size() > 1) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(bookings.get(1)));
            } else {
                itemDto.setLastBooking(BookingMapper.toBookingDto(nextBooking));
            }
        }
    }
}