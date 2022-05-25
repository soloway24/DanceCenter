package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.implementations.ArtistService;
import com.kuznets.danceCenter.services.implementations.SongService;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
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
@RequestMapping("/artists")
public class ArtistController {


    private ArtistServiceInterface artistService;
    private SongServiceInterface songService;


    private static Logger logger = LogManager.getLogger(ArtistController.class);

    @Autowired
    public ArtistController(ArtistService artistService, SongService songService) {
        this.artistService = artistService;
        this.songService = songService;
    }

    @PostMapping("/add")
    public RedirectView addArtist(@RequestParam String name, @RequestParam Set<Song> songs, RedirectAttributes redir){
        RedirectView redirectView= new RedirectView("/",true);
        String notification = "Виконавець '"+ name +"' був успішно доданий!";
        boolean success =  artistService.addArtist(name, songs);
// logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }

//    @PostMapping("/update")
//    public RedirectView editArtist(@RequestParam Long id, @RequestParam String name, @RequestParam Set<Song> songs, RedirectAttributes redir){
//        RedirectView redirectView = new RedirectView("/",true);
//        String notification = "Виконавець '"+ name +"' був успішно доданий!";
//        boolean success =  artistService.addArtist(name, songs);
//// logging
//        redir.addFlashAttribute("success", success);
//        redir.addFlashAttribute("notification", notification);
//        return redirectView;
//    }

    @PostMapping("/delete")
    public RedirectView deleteArtist(@RequestParam Long id, Model model, RedirectAttributes redir) throws Exception {
        RedirectView redirectView = new RedirectView("/",true);
        Artist artist = artistService.getArtistById(id);
        String name = artist.getName();

        String notification = "Виконавець '"+ name +"' був успішно видалений!";

        boolean success =  artistService.deleteArtist(id);
//logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }
    

}
