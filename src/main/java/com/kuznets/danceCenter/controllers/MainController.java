package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.DecimalMin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @GetMapping(value = {"/addPost", "/addPost/{postSongsIds}"})
    public String addPostPage(Model model, @PathVariable(name = "postSongsIds", required = false) String postSongsIds){
        if(postSongsIds != null){
            List<Long> ids = Arrays.stream(postSongsIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<Long> existingIds = songService.removeNonExistentIds(ids);
            if(existingIds.size() == 0) return "redirect:/addPost";
            if(ids.size() == existingIds.size()){
                ArrayList<Song> postSongs = (ArrayList<Song>) ids.stream().map(id -> {
                    try {
                        return songService.getSongById(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
                model.addAttribute("postSongs", postSongs);
            }else{
                String urlIds = songService.idListToString(existingIds);
                System.out.println(urlIds);
                return "redirect:/addPost/"+urlIds;
            }
        }
        model.addAttribute("appName",appName);
        model.addAttribute("songs",songService.getAll());
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("posts", postService.getAll());
        return "addPostPage";
    }


}
