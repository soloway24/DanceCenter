package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/posts")
public class PostController {

    private PostServiceInterface postService;
    private SongServiceInterface songService;
    private static Logger logger = LogManager.getLogger(PostController.class);

    @Autowired
    public PostController(PostServiceInterface postService, SongServiceInterface songService) {
        this.postService = postService;
        this.songService = songService;
    }

    @PostMapping("/add")
    public RedirectView addPost(@RequestParam String description, @RequestParam("songIds") String songsIdsUnparsed, RedirectAttributes redir){
        RedirectView redirectView= new RedirectView("/",true);
        String notification = "Публікація '"+description+"' була успішно доданий!";
        List<Long> ids = Utils.stringToIdList(songsIdsUnparsed);
        Set<Song> songs = new HashSet<>();
        for(Long id : ids){
            try{
                Song song = songService.getSongById(id);
                songs.add(song);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
