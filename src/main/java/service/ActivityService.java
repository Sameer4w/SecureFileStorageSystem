package com.example.securefilestoragesystem.service;

import com.example.securefilestoragesystem.entity.Activity;
import com.example.securefilestoragesystem.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public void saveActivity(String email,
                             String fileName,
                             String type){

        Activity activity = new Activity();

        activity.setUserEmail(email);
        activity.setFileName(fileName);
        activity.setActivityType(type);
        activity.setActivityTime(LocalDateTime.now());

        activityRepository.save(activity);

    }

    public List<Activity> getRecentActivities(String email){

        return activityRepository
                .findTop5ByUserEmailOrderByActivityTimeDesc(email);

    }

}