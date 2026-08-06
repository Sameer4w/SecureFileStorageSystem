package com.example.securefilestoragesystem.controller;

import com.example.securefilestoragesystem.entity.User;
import com.example.securefilestoragesystem.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(){

        return "home";

    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session){

        if(session.getAttribute("userEmail") != null){
            return "redirect:/dashboard";
        }

        return "login";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {

        String userEmail = (String) session.getAttribute("userEmail");

        if(userEmail == null){
            return "redirect:/login";
        }

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if(optionalUser.isPresent()){
            model.addAttribute("user", optionalUser.get());
        }

        return "profile";
    }
}