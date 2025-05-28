package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long userId);

    List<Item> findAllByNameContainingIgnoreCase(String text);

    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT i FROM Item i WHERE (i.name ILIKE %:text% OR i.description ILIKE %:text%) AND i.available = true")
    List<Item> findItemsByNameOrDescription(@Param("text") String text);
}