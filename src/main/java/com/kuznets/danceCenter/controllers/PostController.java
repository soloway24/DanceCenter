package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/posts")
public class PostController {

    private PostServiceInterface postService;
    private SongServiceInterface songService;

    @Autowired
    public PostController(PostServiceInterface postService, SongServiceInterface songService) {
        this.postService = postService;
        this.songService = songService;
    }

    @GetMapping
    public String posts(Model model) {
        model.addAttribute("songs",songService.getAll());
        model.addAttribute("posts", postService.getAll());
        return "posts";
    }


    @PostMapping("/add")
    public RedirectView addPost(@RequestParam String description, @RequestParam("songIds") String songsIdsUnparsed, RedirectAttributes redir){
        List<Long> ids = songsIdsUnparsed.length()>2 ? Utils.stringToIdList(songsIdsUnparsed) : new ArrayList<>();
        if(ids.size() == 0)
            return new RedirectView("/errors/addPost",true);

        boolean success;
        String notification;
        try {
            List<Song> songs = songService.getSongsByIds(ids);
            postService.addPost(description, songs);
            notification = "Публікація '"+description+"' була успішно додана!";
            success = true;
        } catch (Exception e) {
            notification = "Публікація '"+description+"' не була додана!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        if(success){
            return new RedirectView("/",true);
        }else
            return new RedirectView("/errors/addPost",true);
    }


    @PostMapping("/delete")
    public RedirectView deletePost(@RequestParam Long id, RedirectAttributes redir) {
        boolean success;
        String notification;

        try {
            postService.deletePostById(id);
            notification = "Публікація '"+ id +"' була успішно видалена!";
            success = true;
        } catch (Exception e) {
            notification = "Публікація '"+ id +"' не була видалена!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);

        if(success){
            return new RedirectView("/",true);
        }else
            return new RedirectView("/errors/error",true);
    }
}
