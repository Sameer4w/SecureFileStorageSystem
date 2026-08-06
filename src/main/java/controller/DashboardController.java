package com.example.securefilestoragesystem.controller;

import com.example.securefilestoragesystem.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.securefilestoragesystem.service.ActivityService;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String userName = (String) session.getAttribute("userName");
        String email = (String) session.getAttribute("userEmail");

        model.addAttribute("name", userName);

        int totalFiles = dashboardService.getTotalFiles(email);

        long totalBytes = dashboardService.getTotalStorage(email);

        double totalMB = totalBytes / (1024.0 * 1024.0);

        double totalLimitMB = 1024.0;

        double usedPercentage = (totalMB / totalLimitMB) * 100;

        if(usedPercentage > 100){
            usedPercentage = 100;
        }

        String lastUpload = dashboardService.getLastUploadedFile(email);

        model.addAttribute("totalFiles", totalFiles);
        model.addAttribute("storage",
                String.format("%.2f MB", totalMB));
        model.addAttribute("lastUpload", lastUpload);
        model.addAttribute("usedStorage", totalMB);

// Assuming 100 MB is the total quota
        model.addAttribute("freeStorage", 100 - totalMB);

// Sample data for now
        model.addAttribute("pdfCount", 12);
        model.addAttribute("imageCount", 8);
        model.addAttribute("docCount", 5);
        model.addAttribute("usedMB",
                String.format("%.2f", totalMB));

        model.addAttribute("totalLimitMB",
                String.format("%.0f", totalLimitMB));

        model.addAttribute("usedPercentage",
                usedPercentage);

        model.addAttribute(
                "activities",
                activityService.getRecentActivities(email)
        );

        return "dashboard";

    }
}