package com.example.securefilestoragesystem.controller;

import com.example.securefilestoragesystem.entity.ShareLink;
import com.example.securefilestoragesystem.service.ShareLinkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.securefilestoragesystem.service.ActivityService;
import com.example.securefilestoragesystem.service.EmailService;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ShareController {

    @Autowired
    private ShareLinkService shareLinkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private EmailService emailService;

    @ResponseBody
    @GetMapping("/share/create/{fileName}")
    public String createShareLink(@PathVariable String fileName,
                                  HttpSession session) {

        String userEmail = (String) session.getAttribute("userEmail");

        ShareLink link = shareLinkService.createShareLink(
                fileName,
                userEmail,
                24
        );

        activityService.saveActivity(
                userEmail,
                fileName,
                "Shared"
        );

        String shareUrl =
                "http://localhost:8080/share/" + link.getToken();

        emailService.sendEmail(

                userEmail,

                "File Shared Successfully",

                "Hello,\n\n"

                        + "You have successfully shared your file:\n\n"

                        + fileName +

                        "\n\nShare Link:\n"

                        + shareUrl +

                        "\n\nThis link will expire in 24 hours."

        );

        return shareUrl;
    }

    @ResponseBody
    @GetMapping("/share/{token}")
    public String openSharedFile(@PathVariable String token) {

        ShareLink link = shareLinkService.getShareLink(token);

        if (link == null) {
            return "Invalid or Expired Share Link";
        }

        return """
                <h2>Shared File</h2>
                <p>%s</p>

                <a href="/shared/download/%s">
                    <button>Download</button>
                </a>
                """.formatted(link.getFileName(), token);
    }

    @GetMapping("/shared/download/{token}")
    @ResponseBody
    public ResponseEntity<Resource> downloadSharedFile(
            @PathVariable String token)
            throws MalformedURLException {

        ShareLink link = shareLinkService.getShareLink(token);

        if (link == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                link.getOwnerEmail(),
                link.getFileName()
        );

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }

}