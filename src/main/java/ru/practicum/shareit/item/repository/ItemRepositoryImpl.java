//package ru.practicum.shareit.item.repository;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.item.model.Item;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@Slf4j
//public class ItemRepositoryImpl implements ItemRepository {
//    private final List<Item> items = new ArrayList<>();
//
//    @Override
//    public Item insert(Item newItem) {
//        newItem.setId(getNextId());
//        items.add(newItem);
//        log.info("Создана новая вещь: {}", newItem);
//        return newItem;
//    }
//
//    @Override
//    public Optional<Item> findById(long itemId) {
//        Optional<Item> itemOpt = items.stream()
//                .filter(item -> item.getId() == itemId)
//                .findFirst();
//        log.info("Вещь с id: {} найдена", itemId);
//        return itemOpt;
//    }
//
//    @Override
//    public List<Item> findAllByUserId(long userId) {
//        List<Item> itemsOpt = items.stream()
//                .filter(item -> item.getOwnerId() == userId)
//                .toList();
//        log.info("Вещи с id пользователя: {} найдены", userId);
//        return itemsOpt;
//    }
//
//    @Override
//    public List<Item> findAllByText(String text) {
//        List<Item> result = items.stream()
//                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()))
//                .toList();
//        log.info("Вещи по тексту поиска: {} найдены", text);
//        return result;
//    }
//
//    @Override
//    public void update(long itemId, Item updatedItem) {
//        int index = items.indexOf(findById(itemId)
//                .orElseThrow(() -> {
//                    log.error("Вещь с id: {} не найдена", itemId);
//                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
//                }));
//        items.set(index, updatedItem);
//        log.info("Обновлена вещь: {}", updatedItem);
//    }
//
//    @Override
//    public void deleteById(long itemId) {
//        items.removeIf(item -> item.getId() == itemId);
//        log.info("Удалена вещь с id: {}", itemId);
//    }
//
//    private long getNextId() {
//        long currentMaxId = items.stream()
//                .mapToLong(Item::getId)
//                .max()
//                .orElse(0);
//        return ++currentMaxId;
//    }
//}