package com.example.securefilestoragesystem.service;

import com.example.securefilestoragesystem.entity.ShareLink;
import com.example.securefilestoragesystem.repository.ShareLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareLinkService {

    @Autowired
    private ShareLinkRepository shareLinkRepository;

    public ShareLink createShareLink(String fileName,
                                     String ownerEmail,
                                     int expiryHours) {

        ShareLink link = new ShareLink();

        String token = UUID.randomUUID().toString().replace("-", "");

        link.setToken(token);
        link.setFileName(fileName);
        link.setOwnerEmail(ownerEmail);

        if (expiryHours == 0) {
            link.setExpiryTime(null);
        } else {
            link.setExpiryTime(LocalDateTime.now().plusHours(expiryHours));
        }

        return shareLinkRepository.save(link);
    }

    public ShareLink getShareLink(String token) {

        Optional<ShareLink> optional = shareLinkRepository.findByToken(token);

        if (optional.isEmpty()) {
            return null;
        }

        ShareLink link = optional.get();

        if (link.getExpiryTime() != null &&
                link.getExpiryTime().isBefore(LocalDateTime.now())) {

            return null;
        }

        return link;
    }

}