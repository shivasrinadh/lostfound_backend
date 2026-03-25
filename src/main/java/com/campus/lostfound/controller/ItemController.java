package com.campus.lostfound.controller;

import com.campus.lostfound.dto.ItemDTO;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * GET /api/items
     * Public — paginated list with optional filters.
     *
     * @param type     LOST | FOUND
     * @param status   OPEN | CLAIMED | CLOSED
     * @param category ELECTRONICS | CLOTHING | BAGS | …
     * @param keyword  full-text search on title / description / location
     * @param page     0-based page index  (default 0)
     * @param size     page size           (default 10)
     */
    @GetMapping
    public ResponseEntity<Page<ItemDTO>> getAllItems(
            @RequestParam(required = false) Item.ItemType  type,
            @RequestParam(required = false) Item.Status    status,
            @RequestParam(required = false) Item.Category  category,
            @RequestParam(required = false) String         keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                itemService.getAllItems(type, status, category, keyword, pageable));
    }

    /**
     * GET /api/items/{id}
     * Public.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    /**
     * GET /api/items/my
     * Authenticated — items reported by the current user.
     */
    @GetMapping("/my")
    public ResponseEntity<Page<ItemDTO>> getMyItems(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                itemService.getMyItems(userDetails.getUsername(), pageable));
    }

    /**
     * POST /api/items
     * Authenticated — report a lost or found item.
     */
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(
            @Valid @RequestBody ItemDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(dto, userDetails.getUsername()));
    }

    /**
     * PUT /api/items/{id}
     * Authenticated — owner or admin only.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                itemService.updateItem(id, dto, userDetails.getUsername()));
    }

    /**
     * DELETE /api/items/{id}
     * Authenticated — owner or admin only.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        itemService.deleteItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
