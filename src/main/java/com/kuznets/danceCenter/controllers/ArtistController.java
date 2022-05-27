package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.implementations.ArtistService;
import com.kuznets.danceCenter.services.implementations.SongService;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/artists")
public class ArtistController {


    private ArtistServiceInterface artistService;
    private SongServiceInterface songService;


    @Autowired
    public ArtistController(ArtistService artistService, SongService songService) {
        this.artistService = artistService;
        this.songService = songService;
    }

    @PostMapping("/add")
    public RedirectView addArtist(@RequestParam Long id, @RequestParam String name,
                                   @RequestParam(name = "songIds") String songsIdsUnparsed,
                                   HttpServletRequest request, RedirectAttributes redir) {

        List<Long> ids = Utils.stringToIdList(songsIdsUnparsed);
        String notification;
        boolean success;
        try {
            List<Song> songs = songService.getSongsByIds(ids);
            artistService.addArtist(name, songs);
            notification = "Виконавець '"+ name +"' був успішно оновлений!";
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            notification = "Виконавець '"+ name +"' не був оновлений!";
            success = false;
        }
        String referer = request.getHeader("Referer");
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return new RedirectView(referer,true);
    }

//    @PostMapping("/update")
//    public RedirectView updateArtist(@RequestParam Long id, @RequestParam String name,
//                                   @RequestParam(name = "songIds") String songsIdsUnparsed,
//                                   HttpServletRequest request, RedirectAttributes redir) {
//
//        List<Long> ids = Utils.stringToIdList(songsIdsUnparsed);
//        List<Song> songs = new ArrayList<>();
//        for(Long sid : ids){
//            try{
//                Song song = songService.getSongById(sid);
//                songs.add(song);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        String notification;
//        boolean success;
//        try {
//            artistService.addArtist(name, songs);
//            notification = "Виконавець '"+ name +"' був успішно оновлений!";
//            success = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            notification = "Виконавець '"+ name +"' не був оновлений!";
//            success = false;
//        }
//        String referer = request.getHeader("Referer");
//        redir.addFlashAttribute("success", success);
//        redir.addFlashAttribute("notification", notification);
//        return new RedirectView(referer,true);
//    }

    @PostMapping("/delete")
    public RedirectView deleteArtist(@RequestParam Long id, Model model, RedirectAttributes redir) throws Exception {
        RedirectView redirectView = new RedirectView("/",true);
        Artist artist = artistService.getArtistById(id);
        String name = artist.getName();

        String notification = "Виконавець '"+ name +"' був успішно видалений!";

        boolean success =  artistService.deleteArtist(id);
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }
    

}
