package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping("/header")
    public String header(){
        return "header";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }

    @GetMapping("/addPost")
    public String addPostPage(Model model, @RequestParam(name = "postSongsIds", required = false) String postSongsIds,
                              @RequestParam(name = "description", required = false) String description, RedirectAttributes redir){

        if(postSongsIds != null){
            List<Long> ids = Arrays.stream(postSongsIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<Long> existingIds = songService.removeNonExistentIds(ids);
            if(existingIds.size() == 0) {
                if(description != null)
                    return "redirect:/addPost?description="+description;
                else return "redirect:/addPost";
            }
            if(ids.size() == existingIds.size()){
                ArrayList<Song> postSongs = null;
                try {
                    postSongs = (ArrayList<Song>) songService.getSongsByIds(ids);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(description != null){
                    model.addAttribute("description", description);
                }
                model.addAttribute("postSongs", postSongs);
            }else{
                String urlIds = Utils.idListToString(existingIds);
                System.out.println(urlIds);
                if(description != null){
                    return "redirect:/addPost?postSongsIds="+urlIds+"&description="+description;
                }
                else return "redirect:/addPost?postSongsIds="+urlIds;
            }
        }else {
            if(description != null){
                model.addAttribute("description", description);
            }
        }

        model.addAttribute("appName",appName);
        model.addAttribute("songs",songService.getAll());
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("posts", postService.getAll());
        return "addPostPage";
    }

}
