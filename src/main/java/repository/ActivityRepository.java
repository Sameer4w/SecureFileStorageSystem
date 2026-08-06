package com.example.securefilestoragesystem.repository;

import com.example.securefilestoragesystem.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findTop5ByUserEmailOrderByActivityTimeDesc(
            String userEmail
    );

}