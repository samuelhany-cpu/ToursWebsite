package com.naghamtours.repository;

import com.naghamtours.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByEnabledTrue();
    List<Package> findByLocationContainingIgnoreCase(String location);
    List<Package> findByStartDateAfter(LocalDateTime date);
    List<Package> findByPriceLessThanEqual(Double price);
    List<Package> findByMaxParticipantsGreaterThanEqual(Integer participants);
    List<Package> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}