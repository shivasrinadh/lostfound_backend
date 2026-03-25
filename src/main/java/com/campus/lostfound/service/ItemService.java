package com.campus.lostfound.service;

import com.campus.lostfound.dto.ItemDTO;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@SuppressWarnings("null")
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // ── Create ────────────────────────────────────────────────────────

    @Transactional
    public ItemDTO createItem(ItemDTO dto, String username) {
        User user = findUser(username);

        Item item = Item.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .category(dto.getCategory())
                .type(dto.getType())
                .status(Item.Status.OPEN)
                .dateOccurred(dto.getDateOccurred())
                .imageUrl(dto.getImageUrl())
                .reportedBy(user)
                .build();

        Item saved = Objects.requireNonNull(itemRepository.save(item));
        return ItemDTO.fromEntity(saved);
    }

    // ── Read ──────────────────────────────────────────────────────────

    public Page<ItemDTO> getAllItems(Item.ItemType type, Item.Status status,
            Item.Category category, String keyword,
            Pageable pageable) {
        return itemRepository
                .searchItems(type, status, category, keyword, pageable)
                .map(ItemDTO::fromEntity);
    }

    public ItemDTO getItemById(Long id) {
        return ItemDTO.fromEntity(findItem(id));
    }

    public Page<ItemDTO> getMyItems(String username, Pageable pageable) {
        User user = findUser(username);
        Long userId = Objects.requireNonNull(user.getId());
        return itemRepository.findByReportedById(userId, pageable)
                .map(ItemDTO::fromEntity);
    }

    // ── Update ────────────────────────────────────────────────────────

    @Transactional
    public ItemDTO updateItem(Long id, ItemDTO dto, String username) {
        Item item = findItem(id);
        User user = findUser(username);

        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        boolean isOwner = item.getReportedBy().getId().equals(user.getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to update this item");
        }

        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setLocation(dto.getLocation());
        item.setCategory(dto.getCategory());
        item.setType(dto.getType());
        item.setDateOccurred(dto.getDateOccurred());
        item.setImageUrl(dto.getImageUrl());
        if (dto.getStatus() != null) {
            item.setStatus(dto.getStatus());
        }

        Item updated = Objects.requireNonNull(itemRepository.save(item));
        return ItemDTO.fromEntity(updated);
    }

    // ── Delete ────────────────────────────────────────────────────────

    @Transactional
    public void deleteItem(Long id, String username) {
        Item item = findItem(id);
        User user = findUser(username);

        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        boolean isOwner = item.getReportedBy().getId().equals(user.getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not allowed to delete this item");
        }

        itemRepository.delete(item);
    }

    // ── Private helpers ───────────────────────────────────────────────

    private Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}
