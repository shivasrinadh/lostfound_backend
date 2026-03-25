package com.campus.lostfound.controller;

import com.campus.lostfound.dto.ClaimDTO;
import com.campus.lostfound.model.Claim;
import com.campus.lostfound.service.ClaimService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitClaimMultipart(
            @RequestParam Long itemId,
            @RequestParam String proofDescription,
            @RequestParam(required = false) MultipartFile document,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = extractUsername(userDetails);
        if (username == null) {
            return unauthorized("Please login before submitting a claim.");
        }

        if (itemId == null) {
            return badRequest("itemId is required.");
        }
        if (!StringUtils.hasText(proofDescription)) {
            return badRequest("proofDescription is required.");
        }

        try {
            ClaimDTO dto = new ClaimDTO();
            dto.setItemId(itemId);
            dto.setProofDescription(proofDescription.trim());

            ClaimDTO saved = claimService.submitClaim(dto, document, image, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return badRequest(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Failed to submit claim. Please try again."));
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitClaimJson(
            @RequestBody ClaimDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = extractUsername(userDetails);
        if (username == null) {
            return unauthorized("Please login before submitting a claim.");
        }

        if (dto == null || dto.getItemId() == null) {
            return badRequest("itemId is required.");
        }
        if (!StringUtils.hasText(dto.getProofDescription())) {
            return badRequest("proofDescription is required.");
        }

        try {
            dto.setProofDescription(dto.getProofDescription().trim());
            ClaimDTO saved = claimService.submitClaim(dto, null, null, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return badRequest(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Failed to submit claim. Please try again."));
        }
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ClaimDTO>> getClaimsForItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(claimService.getClaimsForItem(itemId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ClaimDTO>> getMyClaims(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = extractUsername(userDetails);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by("claimedAt").descending());
        return ResponseEntity.ok(claimService.getMyClaims(username, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClaimDTO>> getAllClaims(
            @RequestParam(required = false) Claim.ClaimStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("claimedAt").descending());
        return ResponseEntity.ok(claimService.getAllClaims(status, pageable));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveClaim(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = extractUsername(userDetails);
        if (username == null) {
            return unauthorized("Please login as admin.");
        }

        try {
            String statusValue = body == null ? null : body.get("status");
            if (!StringUtils.hasText(statusValue)) {
                return badRequest("status is required.");
            }

            Claim.ClaimStatus resolution;
            try {
                resolution = Claim.ClaimStatus.valueOf(statusValue.trim().toUpperCase());
            } catch (Exception e) {
                return badRequest("Invalid claim status.");
            }

            String adminNote = body.getOrDefault("adminNote", null);
            ClaimDTO dto = claimService.resolveClaim(id, resolution, adminNote, username);
            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return badRequest(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Failed to resolve claim."));
        }
    }

    private String extractUsername(UserDetails userDetails) {
        if (userDetails == null || !StringUtils.hasText(userDetails.getUsername())) {
            return null;
        }
        return userDetails.getUsername();
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        return ResponseEntity.badRequest().body(error(message));
    }

    private ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error(message));
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", false);
        res.put("message", message);
        return res;
    }
}