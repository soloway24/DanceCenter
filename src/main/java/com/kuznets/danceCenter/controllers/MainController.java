package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.services.implementations.UserDetailsServiceImpl;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Transactional
public class MainController {

    private final SongServiceInterface songService;
    private final ArtistServiceInterface artistService;
    private final PostServiceInterface postService;
    private final UserServiceInterface userService;
    private final UserDetailsServiceImpl userDetailsService;

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

    @GetMapping("/search")
    public String search(@RequestParam String searchQuery, Model model) {
        model.addAttribute("users", userService.getByUsernameText(searchQuery));
        model.addAttribute("artists", artistService.getByNameText(searchQuery));
        model.addAttribute("songs", songService.getBySearchText(searchQuery));
        model.addAttribute("searchQuery", searchQuery);
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        return "searchResult";
    }
}
