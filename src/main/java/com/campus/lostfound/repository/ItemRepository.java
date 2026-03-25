package com.campus.lostfound.repository;

import com.campus.lostfound.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByType(Item.ItemType type, Pageable pageable);

    Page<Item> findByTypeAndStatus(Item.ItemType type, Item.Status status, Pageable pageable);

    Page<Item> findByReportedById(Long userId, Pageable pageable);

    List<Item> findByCategory(Item.Category category);

    @Query("SELECT i FROM Item i WHERE " +
           "(:type     IS NULL OR i.type     = :type)     AND " +
           "(:status   IS NULL OR i.status   = :status)   AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:keyword  IS NULL OR LOWER(i.title)       LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "                   OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "                   OR LOWER(i.location)    LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Item> searchItems(
            @Param("type")     Item.ItemType  type,
            @Param("status")   Item.Status    status,
            @Param("category") Item.Category  category,
            @Param("keyword")  String         keyword,
            Pageable pageable);
}
