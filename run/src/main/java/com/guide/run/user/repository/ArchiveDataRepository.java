package com.guide.run.user.repository;

import com.guide.run.user.entity.ArchiveData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveDataRepository extends JpaRepository<ArchiveData, String> {
}