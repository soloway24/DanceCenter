package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.implementations.ArtistService;
import com.kuznets.danceCenter.services.implementations.SongService;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private UserServiceInterface userService;

    @Autowired
    public ArtistController(ArtistServiceInterface artistService, SongServiceInterface songService,
                            UserServiceInterface userService) {
        this.artistService = artistService;
        this.songService = songService;
        this.userService = userService;
    }

    private void populateModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        AppUser appUser = userService.getUserByUsername(user.getUsername());
        model.addAttribute("artists", artistService.getAll());
        model.addAttribute("currentUser", appUser);
    }

    private void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        AppUser appUser = userService.getUserByUsername(user.getUsername());
        model.addAttribute("currentUser", appUser);
    }


    @GetMapping
    public String artists(Model model) {
        populateModel(model);
        return "artists";
    }

    @GetMapping("/{artistId}")
    public String artist(@PathVariable("artistId") Long artistId, Model model) {
        try {
            Artist artist = artistService.getArtistById(artistId);
            ArrayList<Artist> artists = new ArrayList<>();
            artists.add(artist);
            model.addAttribute("artists", artists);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "errorPage";
        }
        addUserToModel(model);
        return "artists";
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
