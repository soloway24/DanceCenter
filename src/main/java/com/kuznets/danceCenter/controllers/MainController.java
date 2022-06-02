package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.services.implementations.UserDetailsServiceImpl;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import com.kuznets.danceCenter.utils.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Transactional
public class MainController {

    private SongServiceInterface songService;
    private ArtistServiceInterface artistService;
    private PostServiceInterface postService;
    private UserServiceInterface userService;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public MainController(SongServiceInterface songService, ArtistServiceInterface artistService,
                          PostServiceInterface postService, UserServiceInterface userService,
                          UserDetailsServiceImpl userDetailsService) {
        this.songService = songService;
        this.artistService = artistService;
        this.postService = postService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }


    private void populateModel(Model model) {
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        model.addAttribute("songs", songService.getAll());
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("posts", postService.getAll());
    }


    @GetMapping("/")
    public String main(){
        return "redirect:/posts/feed";
    }

    @GetMapping("/admin")
    public String admin(Model model){
        populateModel(model);
        return "adminPage";
    }

    @GetMapping("/about")
    public String about(Model model) {
        Utils.addAppNameToModel(model);
        userDetailsService.addUserToModel(model);
        return "about";
    }
}
