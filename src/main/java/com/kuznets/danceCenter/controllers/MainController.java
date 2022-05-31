package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private SongServiceInterface songService;
    private ArtistServiceInterface artistService;
    private PostServiceInterface postService;
    private UserServiceInterface userService;

    public MainController(SongServiceInterface songService, ArtistServiceInterface artistService,
                          PostServiceInterface postService, UserServiceInterface userService) {
        this.songService = songService;
        this.artistService = artistService;
        this.postService = postService;
        this.userService = userService;
    }

    @Value("${spring.application.name}")
    private String appName;

    private void populateModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        AppUser appUser = userService.getUserByUsername(user.getUsername());
        model.addAttribute("appName",appName);
        model.addAttribute("songs",songService.getAll());
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("posts", postService.getAll());
        model.addAttribute("currentUser", appUser);
    }

    private void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        AppUser appUser = userService.getUserByUsername(user.getUsername());
        model.addAttribute("currentUser", appUser);
    }

    @GetMapping("/")
    public String showMain(Model model){
        populateModel(model);
        return "mainPage";
    }

    @GetMapping("/about")
    public String about(Model model) {
        addUserToModel(model);
        return "about";
    }



}
