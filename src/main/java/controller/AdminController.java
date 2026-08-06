package com.example.securefilestoragesystem.controller;

import com.example.securefilestoragesystem.entity.User;
import com.example.securefilestoragesystem.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin")

    public String admin(Model model){

        List<User> users=userRepository.findAll();

        int totalUsers=users.size();

        int totalFiles=0;

        long storage=0;

        File uploadFolder=new File(
                System.getProperty("user.dir")+"/uploads"
        );

        if(uploadFolder.exists()){

            File[] folders=uploadFolder.listFiles();

            if(folders!=null){

                for(File folder:folders){

                    File[] files=folder.listFiles();

                    if(files!=null){

                        totalFiles+=files.length;

                        for(File f:files){

                            storage+=f.length();

                        }

                    }

                }

            }

        }

        model.addAttribute("users",users);

        model.addAttribute("totalUsers",totalUsers);

        model.addAttribute("totalFiles",totalFiles);

        model.addAttribute("storage",
                String.format("%.2f MB",storage/(1024.0*1024)));

        return "admin";

    }

}