package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.services.implementations.UserDetailsServiceImpl;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
@RequestMapping("/artists")
@Transactional
public class ArtistController {

    private final ArtistServiceInterface artistService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public ArtistController(ArtistServiceInterface artistService, UserDetailsServiceImpl userDetailsService) {
        this.artistService = artistService;
        this.userDetailsService = userDetailsService;
    }


    private void populateModel(Model model) {
        userDetailsService.addUserToModel(model);
        model.addAttribute("artists", artistService.getAll());
        Utils.addAppNameToModel(model);
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
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        return "artists";
    }

    @PostMapping("/delete")
    public RedirectView deleteArtist(@RequestParam Long id, HttpServletRequest request, RedirectAttributes redir) throws Exception {
        boolean success;
        String notification;
        try {
            Artist artist = artistService.getArtistById(id);
            String name = artist.getName();
            artistService.deleteArtist(id);
            notification = "Artist '"+ name +"' has been deleted!";
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            redir.addFlashAttribute("error", e.getMessage());
            notification = "Artist (id='"+ id +"') has not been deleted!";
            success = false;
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        if(success) {
            String referer = request.getHeader("Referer");
            return new RedirectView(referer, true);
        } else {
            return new RedirectView("/errors/error", true);
        }
    }
}
