package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Set;

@Controller
@RequestMapping("/posts")
public class PostController {

    private PostServiceInterface postService;
    private static Logger logger = LogManager.getLogger(PostController.class);

    @Autowired
    public PostController(PostServiceInterface postService) {
        this.postService = postService;
    }

    @PostMapping("/add")
    public RedirectView addPost(@RequestParam String description, @RequestParam Set<Song> songs, Model model, RedirectAttributes redir){
        RedirectView redirectView= new RedirectView("/",true);
        String notification = "Публікація '"+description+"' була успішно доданий!";
        boolean success =  postService.addPost(description, songs);
// logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }


    @PostMapping("/delete")
    public RedirectView deletePost(@RequestParam Long id, Model model, RedirectAttributes redir) throws Exception {
        RedirectView redirectView = new RedirectView("/",true);
        String description = postService.getPostById(id).getDescription();
        String notification = "Публікація '"+ description +"' була успішно видалена!";
        boolean success =  postService.deletePost(id);
//logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }


}
