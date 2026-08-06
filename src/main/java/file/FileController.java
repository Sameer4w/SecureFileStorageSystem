package com.example.securefilestoragesystem.file;
import com.example.securefilestoragesystem.service.ActivityService;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.ui.Model;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;

import java.net.MalformedURLException;
import com.example.securefilestoragesystem.service.EmailService;

import com.example.securefilestoragesystem.service.EncryptionService;

@Controller
public class FileController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @ResponseBody
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             HttpSession session) throws IOException {

        String userEmail = (String) session.getAttribute("userEmail");
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            return "Invalid File";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        List<String> allowedExtensions = Arrays.asList(
                "pdf",
                "doc",
                "docx",
                "txt",
                "jpg",
                "jpeg",
                "png"
        );

        if (!allowedExtensions.contains(extension)) {
            return "Invalid file type.\nOnly PDF, DOC, DOCX, TXT, JPG, JPEG and PNG files are allowed.";
        }
        System.out.println("Logged in User: " + userEmail);

        String uploadDir = System.getProperty("user.dir")
                + File.separator
                + "uploads"
                + File.separator
                + userEmail;

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        long maxSize = 10 * 1024 * 1024; // 10 MB

        if (file.getSize() > maxSize) {
            return "File size exceeds 10 MB limit.";
        }
        Path destination = uploadPath.resolve(file.getOriginalFilename());

        byte[] originalData = file.getBytes();

        byte[] encryptedData;

        try {
            encryptedData = encryptionService.encrypt(originalData);
        } catch (Exception e) {
            return "Encryption Failed";
        }

        Files.write(destination, encryptedData);

        activityService.saveActivity(
                userEmail,
                fileName,
                "Uploaded"
        );

        emailService.sendEmail(

                userEmail,

                "File Uploaded Successfully",

                "Hello,\n\n"

                        + "Your file '" + fileName + "' has been uploaded successfully.\n\n"

                        + "Thank you for using Secure File Storage."

        );

        return "File Uploaded Successfully (Encrypted)";
    }

    @GetMapping("/files")
    public String filesPage(Model model, HttpSession session) {

        String userEmail = (String) session.getAttribute("userEmail");

        System.out.println("Session Email = " + userEmail);

        File folder = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads"
                        + File.separator
                        + userEmail
        );
        System.out.println("Folder Path = " + folder.getAbsolutePath());
        File[] files = folder.listFiles();

        if (files != null) {
            for (File f : files) {
                System.out.println(f.getName());
            }
        }



        List<java.util.Map<String, Object>> fileList;

        if (files != null) {

            fileList = Arrays.stream(files)
                    .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
                    .filter(File::isFile)
                    .map(f -> {

                        java.util.Map<String, Object> map = new java.util.HashMap<>();

                        String fileName = f.getName();

                        String lower = fileName.toLowerCase();

                        String icon = "📁";

                        if(lower.endsWith(".pdf")){

                            icon="📕";

                        }
                        else if(lower.endsWith(".doc") ||
                                lower.endsWith(".docx")){

                            icon="📘";

                        }
                        else if(lower.endsWith(".xls") ||
                                lower.endsWith(".xlsx")){

                            icon="📗";

                        }
                        else if(lower.endsWith(".ppt") ||
                                lower.endsWith(".pptx")){

                            icon="📙";

                        }
                        else if(lower.endsWith(".png") ||
                                lower.endsWith(".jpg") ||
                                lower.endsWith(".jpeg") ||
                                lower.endsWith(".gif")){

                            icon="🖼️";

                        }
                        else if(lower.endsWith(".zip") ||
                                lower.endsWith(".rar")){

                            icon="📦";

                        }
                        else if(lower.endsWith(".mp4") ||
                                lower.endsWith(".avi") ||
                                lower.endsWith(".mkv")){

                            icon="🎥";

                        }
                        else if(lower.endsWith(".mp3") ||
                                lower.endsWith(".wav")){

                            icon="🎵";

                        }
                        else if(lower.endsWith(".txt")){

                            icon="📃";

                        }
                        else if(lower.endsWith(".java")){

                            icon="☕";

                        }
                        else if(lower.endsWith(".html")){

                            icon="🌐";

                        }
                        else if(lower.endsWith(".css")){

                            icon="🎨";

                        }
                        else if(lower.endsWith(".js")){

                            icon="📜";

                        }

                        map.put("icon", icon);
                        map.put("name", fileName);
                        map.put("size", f.length());
                        map.put("date", new java.util.Date(f.lastModified()));

                        return map;
                    })
                    .toList();

        } else {
            fileList = List.of();
        }

        model.addAttribute("files", fileList);

        return "files";
    }

    @GetMapping("/search")
    public String searchFiles(@RequestParam String keyword,
                              Model model,
                              HttpSession session) {

        String userEmail = (String) session.getAttribute("userEmail");

        File folder = new File(
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads"
                        + File.separator
                        + userEmail
        );

        File[] files = folder.listFiles();

        List<java.util.Map<String, Object>> fileList;

        if (files != null) {

            fileList = Arrays.stream(files)
                    .filter(File::isFile)
                    .filter(f ->
                            f.getName().toLowerCase()
                                    .contains(keyword.toLowerCase()))
                    .map(f -> {

                        java.util.Map<String, Object> map = new java.util.HashMap<>();

                        String fileName = f.getName();

                        String lower = fileName.toLowerCase();

                        String icon = "📁";

                        if(lower.endsWith(".pdf")){

                            icon="📕";

                        }
                        else if(lower.endsWith(".doc") ||
                                lower.endsWith(".docx")){

                            icon="📘";

                        }
                        else if(lower.endsWith(".xls") ||
                                lower.endsWith(".xlsx")){

                            icon="📗";

                        }
                        else if(lower.endsWith(".ppt") ||
                                lower.endsWith(".pptx")){

                            icon="📙";

                        }
                        else if(lower.endsWith(".png") ||
                                lower.endsWith(".jpg") ||
                                lower.endsWith(".jpeg") ||
                                lower.endsWith(".gif")){

                            icon="🖼️";

                        }
                        else if(lower.endsWith(".zip") ||
                                lower.endsWith(".rar")){

                            icon="📦";

                        }
                        else if(lower.endsWith(".mp4") ||
                                lower.endsWith(".avi") ||
                                lower.endsWith(".mkv")){

                            icon="🎥";

                        }
                        else if(lower.endsWith(".mp3") ||
                                lower.endsWith(".wav")){

                            icon="🎵";

                        }
                        else if(lower.endsWith(".txt")){

                            icon="📃";

                        }
                        else if(lower.endsWith(".java")){

                            icon="☕";

                        }
                        else if(lower.endsWith(".html")){

                            icon="🌐";

                        }
                        else if(lower.endsWith(".css")){

                            icon="🎨";

                        }
                        else if(lower.endsWith(".js")){

                            icon="📜";

                        }

                        map.put("icon", icon);
                        map.put("name", fileName);
                        map.put("size", f.length());
                        map.put("date", new java.util.Date(f.lastModified()));

                        return map;

                    })
                    .toList();

        } else {

            fileList = List.of();

        }

        model.addAttribute("files", fileList);

        return "files";

    }


    @GetMapping("/download/{fileName}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileName,
            HttpSession session) throws Exception {

        String userEmail = (String) session.getAttribute("userEmail");

        if(userEmail == null){
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                userEmail,
                fileName
        );

        if(!Files.exists(path)){
            return ResponseEntity.notFound().build();
        }

        byte[] encryptedBytes = Files.readAllBytes(path);

        byte[] decryptedBytes = encryptionService.decrypt(encryptedBytes);

        String contentType = Files.probeContentType(path);

        if(contentType == null){
            contentType = "application/octet-stream";
        }

        activityService.saveActivity(
                userEmail,
                fileName,
                "Downloaded"
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(decryptedBytes);
    }

    @GetMapping("/preview/{fileName}")
    @ResponseBody
    public ResponseEntity<byte[]> previewFile(
            @PathVariable String fileName,
            HttpSession session) throws Exception {

        String userEmail = (String) session.getAttribute("userEmail");

        Path path = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                userEmail,
                fileName
        );

        if(!Files.exists(path)){
            return ResponseEntity.notFound().build();
        }

        byte[] encryptedBytes = Files.readAllBytes(path);

        byte[] decryptedBytes = encryptionService.decrypt(encryptedBytes);

        String contentType = Files.probeContentType(path);

        if(contentType == null){
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(decryptedBytes);
    }

    @GetMapping("/delete/{name}")
    public String deleteFile(@PathVariable String name,
                             HttpSession session) throws IOException {

        String userEmail = (String) session.getAttribute("userEmail");

        Path filePath = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                userEmail,
                name
        );

        Files.deleteIfExists(filePath);

        activityService.saveActivity(
                userEmail,
                name,
                "Deleted"
        );

        return "redirect:/files";
    }
    @GetMapping("/rename/{name}")
    public String renamePage(@PathVariable String name,
                             Model model) {

        model.addAttribute("oldName", name);

        return "rename";
    }
    @PostMapping("/rename")
    public String renameFile(@RequestParam String oldName,
                             @RequestParam String newName,
                             HttpSession session) throws IOException {

        String userEmail = (String) session.getAttribute("userEmail");

        Path oldPath = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                userEmail,
                oldName
        );

        Path newPath = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                userEmail,
                newName
        );

        Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);

        activityService.saveActivity(
                userEmail,
                newName,
                "Renamed"
        );

        return "redirect:/files";
    }
}