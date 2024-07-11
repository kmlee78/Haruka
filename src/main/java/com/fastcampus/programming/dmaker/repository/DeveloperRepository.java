package com.fastcampus.programming.dmaker.repository;

import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.type.StatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByMemberId(String memberId);

    List<Developer> findDevelopersByStatusCodeEquals(StatusCode statusCode);
}
