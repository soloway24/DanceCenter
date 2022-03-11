package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private SongServiceInterface songService;
    @Autowired
    private ArtistServiceInterface artistService;
    @Autowired
    private PostServiceInterface postService;


    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/")
    public String showMain(Model model){
        model.addAttribute("appName",appName);
        model.addAttribute("songs",songService.getAll());
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("posts", postService.getAll());
        return "mainPage";
    }

}
