package com.campus.lostfound.service;

import com.campus.lostfound.dto.ClaimDTO;
import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.repository.ClaimRepository;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ClaimService(ClaimRepository claimRepository,
                        ItemRepository itemRepository,
                        UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ClaimDTO submitClaim(ClaimDTO dto,
                                MultipartFile document,
                                MultipartFile image,
                                String username) {

        if (dto == null) {
            throw new IllegalArgumentException("Claim payload is required");
        }
        if (dto.getItemId() == null) {
            throw new IllegalArgumentException("Item ID is required");
        }
        if (!StringUtils.hasText(dto.getProofDescription())) {
            throw new IllegalArgumentException("Proof description is required");
        }
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Username is required");
        }

        User user = findUser(username);
        Item item = findItem(dto.getItemId());

        if (item.getStatus() != Item.Status.OPEN) {
            throw new IllegalStateException("This item is no longer available for claims");
        }

        if (item.getReportedBy() != null && item.getReportedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot claim your own item");
        }

        if (claimRepository.existsByItemIdAndClaimedById(item.getId(), user.getId())) {
            throw new IllegalStateException("You have already submitted a claim for this item");
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimedBy(user)
                .proofDescription(dto.getProofDescription().trim())
                .status(Claim.ClaimStatus.PENDING)
                .claimedAt(LocalDateTime.now())
                .build();

        if (document != null && !document.isEmpty()) {
            String documentPath = saveFile(document, "documents");
            claim.setDocumentPath(documentPath);
        }

        if (image != null && !image.isEmpty()) {
            String imagePath = saveFile(image, "images");
            claim.setImagePath(imagePath);
        }

        return ClaimDTO.fromEntity(claimRepository.save(claim));
    }

    private String saveFile(MultipartFile file, String folderType) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            String originalName = file.getOriginalFilename();
            String safeName = StringUtils.hasText(originalName)
                    ? originalName.replaceAll("[\\\\/:*?\"<>|\\s]+", "_")
                    : "file";

            String fileName = UUID.randomUUID() + "_" + safeName;

            // Absolute path: <project-running-dir>/uploads/<folderType>
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", folderType);
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // URL path returned to frontend
            return "/uploads/" + folderType + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public List<ClaimDTO> getClaimsForItem(Long itemId) {
        return claimRepository.findByItemId(itemId)
                .stream()
                .map(ClaimDTO::fromEntity)
                .toList();
    }

    public Page<ClaimDTO> getMyClaims(String username, Pageable pageable) {
        User user = findUser(username);
        return claimRepository.findByClaimedById(user.getId(), pageable)
                .map(ClaimDTO::fromEntity);
    }

    public Page<ClaimDTO> getAllClaims(Claim.ClaimStatus status, Pageable pageable) {
        if (status != null) {
            return claimRepository.findByStatus(status, pageable)
                    .map(ClaimDTO::fromEntity);
        }
        return claimRepository.findAll(pageable).map(ClaimDTO::fromEntity);
    }

    @Transactional
    public ClaimDTO resolveClaim(Long claimId,
                                 Claim.ClaimStatus resolution,
                                 String adminNote,
                                 String adminUsername) {

        User admin = findUser(adminUsername);

        if (admin.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only admins can resolve claims");
        }

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimId));

        if (claim.getStatus() != Claim.ClaimStatus.PENDING) {
            throw new IllegalStateException("Claim is already resolved");
        }

        claim.setStatus(resolution);
        claim.setAdminNote(adminNote);
        claim.setResolvedAt(LocalDateTime.now());

        if (resolution == Claim.ClaimStatus.APPROVED) {
            Item item = claim.getItem();
            item.setStatus(Item.Status.CLAIMED);
            itemRepository.save(item);

            claimRepository.findByItemId(item.getId()).stream()
                    .filter(c -> c.getStatus() == Claim.ClaimStatus.PENDING && !c.getId().equals(claimId))
                    .forEach(c -> {
                        c.setStatus(Claim.ClaimStatus.REJECTED);
                        c.setAdminNote("Another claim was approved for this item");
                        c.setResolvedAt(LocalDateTime.now());
                        claimRepository.save(c);
                    });
        }

        return ClaimDTO.fromEntity(claimRepository.save(claim));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }
}