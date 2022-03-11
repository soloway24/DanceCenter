package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
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
@RequestMapping("/songs")
public class SongController {

    private SongServiceInterface songService;
    private static Logger logger = LogManager.getLogger(SongController.class);

    @Autowired
    public SongController(SongServiceInterface songService) {
        this.songService = songService;
    }

    @PostMapping("/add")
    public RedirectView addSong(@RequestParam String name, @RequestParam Set<Artist> artists, Model model, RedirectAttributes redir){
        RedirectView redirectView= new RedirectView("/",true);
        String notification = "Композиція '"+name+"' була успішно доданий!";
        boolean success =  songService.addSong(name, artists);
// logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }


    @PostMapping("/delete")
    public RedirectView deleteSong(@RequestParam Long id, Model model, RedirectAttributes redir) throws Exception {
        RedirectView redirectView = new RedirectView("/",true);
        String name = songService.getSongById(id).getName();
        String notification = "Композиція '"+ name +"' була успішно видалена!";
        boolean success =  songService.deleteSong(id);
//logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }


}
