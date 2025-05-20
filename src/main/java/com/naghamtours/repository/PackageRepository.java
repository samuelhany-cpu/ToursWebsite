package com.naghamtours.repository;

import com.naghamtours.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByEnabledTrue();
    List<Package> findByLocationContainingIgnoreCase(String location);
    List<Package> findByStartDateAfter(LocalDateTime date);
    List<Package> findByPriceLessThanEqual(Double price);
    List<Package> findByMaxParticipantsGreaterThanEqual(Integer participants);
    List<Package> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Add methods for soft delete
    List<Package> findByDeletedFalse();
    List<Package> findByDeletedFalseAndEnabledTrue();
    Optional<Package> findByIdAndDeletedFalse(Long id);
    
    @Query("UPDATE Package p SET p.deleted = true WHERE p.id = :id")
    @Modifying
    void softDeleteById(@Param("id") Long id);
}