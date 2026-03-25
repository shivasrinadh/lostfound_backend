package com.campus.lostfound.repository;

import com.campus.lostfound.model.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByItemId(Long itemId);

    Page<Claim> findByClaimedById(Long userId, Pageable pageable);

    Page<Claim> findByStatus(Claim.ClaimStatus status, Pageable pageable);

    boolean existsByItemIdAndClaimedById(Long itemId, Long userId);

    long countByStatus(Claim.ClaimStatus status);
}
