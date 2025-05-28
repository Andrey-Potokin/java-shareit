package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, NewItemRequest request) {
        User owner = userService.validateUserExist(userId);
        Item item = toItem(request);
        item.setOwner(owner);
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = validateItemExist(itemId);
        userService.validateUserExist(userId);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        loadDetails(itemDto);

        return itemDto;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest request) {
        userService.validateUserExist(userId);
        Item item = validateItemExist(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Предмет аренды не принадлежит данному пользователю");
        }
        updateItemFields(item, request);
        item = itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    public List<ItemDto> findItemsByOwnerId(Long ownerId) {
        userService.validateUserExist(ownerId);

        List<ItemDto> itemDtos = itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(ItemMapper::toItemDto).toList();

        itemDtos.forEach(this::loadDetails);

        return itemDtos;
    }

    @Override
    public List<ItemDto> findItemsByNameOrDescription(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemsByNameOrDescription(text).stream()
                .map(ItemMapper::toItemDto).toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto) {
        Item item = validateItemExist(itemId);
        User author = userService.validateUserExist(userId);
        validateCommentAuthorAndDate(userId, itemId);

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item validateItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет аренды с id %d не найден.", itemId)));
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

    @Transactional
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

    private void updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
    }
}