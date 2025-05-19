package com.naghamtours.repository;

import com.naghamtours.entity.Reserve;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {
    List<Reserve> findByClient(Client client);
    List<Reserve> findByPackageEntity(Package packageEntity);
    List<Reserve> findByBookingDateBetween(LocalDate start, LocalDate end);
    void deleteByPackageEntity(Package packageEntity);
} 