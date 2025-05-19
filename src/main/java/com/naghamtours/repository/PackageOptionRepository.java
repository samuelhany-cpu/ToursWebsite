package com.naghamtours.repository;

import com.naghamtours.entity.PackageOption;
import com.naghamtours.entity.Package;
import com.naghamtours.entity.PackageOptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageOptionRepository extends JpaRepository<PackageOption, PackageOptionId> {
    List<PackageOption> findByPackageEntity(Package packageEntity);
    void deleteByPackageEntity(Package packageEntity);
} 