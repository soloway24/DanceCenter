package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.implementations.UserDetailsServiceImpl;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Transactional
@RequestMapping("/songs")
public class SongController {

    private final SongServiceInterface songService;
    private final UserServiceInterface userService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SongController(SongServiceInterface songService,
                          UserServiceInterface userService, UserDetailsServiceImpl userDetailsService) {
        this.songService = songService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    private void populateModel(Model model) {
        model.addAttribute("songs", songService.getAll());
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
    }

    @GetMapping
    public String songs(Model model) {
        populateModel(model);
        return "songs";
    }

    @PostMapping("/multipleAdd")
    @ResponseBody
    public List<Long> addMultipleSongs(@RequestParam(name = "titles") String titlesUnparsed, @RequestParam(name = "artists") String artistsUnparsed,
                                       @RequestParam(name = "files") MultipartFile[] files) throws Exception {
        JSONArray titlesJson = new JSONArray(titlesUnparsed);
        JSONArray artistListsJson = new JSONArray(artistsUnparsed);

        if(titlesJson.length() != artistListsJson.length() || titlesJson.length() != files.length)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of titles, artist lists and files must be equal.");

        ArrayList<ArrayList<String>> artists = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();


        int nFiles = titlesJson.length();
        for (int i = 0; i < nFiles; i++) {
            titles.add(titlesJson.get(i).toString());

            JSONArray currentArtistsJson = new JSONArray(artistListsJson.get(i).toString());
            ArrayList<String> currentArtists = new ArrayList<>();
            for(int j = 0; j < currentArtistsJson.length(); j++){
                String curArtist = currentArtistsJson.get(j).toString();
                if(!curArtist.isEmpty() && !curArtist.isBlank()){
                    currentArtists.add(curArtist);
                }
            }
            artists.add(currentArtists);
        }

        ArrayList<Song> songs = new ArrayList<>();

        for(int i = 0; i < nFiles; i++)
            songs.add(songService.addSong(titles.get(i), artists.get(i), files[i]));

        return songs.stream().map(Song::getId).collect(Collectors.toList());
    }

    @PostMapping("/update")
    public RedirectView updateSong(@RequestParam Long id, @RequestParam String title, @RequestParam(required = false) List<String> artists,
                                   HttpServletRequest request, RedirectAttributes redir){
        boolean success;
        String notification;
        try {
            songService.updateSong(id, title, artists);
            notification = "Song (id=" + id + ") has been updated.";
            success = true;
        } catch (Exception e) {
            notification = "Song (id=" + id + ") has not been updated.";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);

        if(success){
            String referer = request.getHeader("Referer");
            return new RedirectView(referer,true);
        }else
            return new RedirectView("/errors/error",true);
    }



    @PostMapping("/delete")
    public RedirectView deleteSong(@RequestParam Long id, HttpServletRequest request, RedirectAttributes redir){
        boolean success;
        String notification;

        try {
            String title = songService.getSongById(id).getTitle();
            songService.deleteSongById(id);
            notification = "Song '"+ title +"'(id=" + id + ") has been deleted!";
            success = true;
        } catch (Exception e) {
            notification = "Song (id=" + id + ") has not been deleted!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);

        if(success){
            String referer = request.getHeader("Referer");
            return new RedirectView(referer,true);
        }else
            return new RedirectView("/errors/error",true);
    }

    @PostMapping("/multipleDelete")
    public RedirectView deleteMultipleSongs(@RequestBody String idsUnparsed, HttpServletRequest request, RedirectAttributes redir){
        List<Long> ids = idsUnparsed.length()>2 ? Utils.stringToIdList(idsUnparsed) : new ArrayList<>();
        if(ids.size() == 0)
            return new RedirectView("/errors/deleteSong",true);

        boolean success;
        String notification;
        try {
            songService.deleteSongsByIds(ids);
            notification = "Songs with IDs: '"+ Arrays.toString(ids.toArray()) +"' have been deleted!";
            success = true;
        } catch (Exception e) {
            notification = "(Not all) Song with IDs: '"+ Arrays.toString(ids.toArray()) +"' have not been deleted!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);

        if(success){
            String referer = request.getHeader("Referer");
            return new RedirectView(referer,true);
        }else
            return new RedirectView("/errors/deleteSong",true);
    }

    @PostMapping("/deleteAll")
    public RedirectView deleteAllSongs(HttpServletRequest request, RedirectAttributes redir){

        songService.deleteAll();

        String notification = "All songs have been deleted!";

        redir.addFlashAttribute("success", true);
        redir.addFlashAttribute("notification", notification);

        String referer = request.getHeader("Referer");
        return new RedirectView(referer,true);
    }

    @PostMapping("/fileInfo")
    @ResponseBody
    public HashMap<String,String> getFileInfo(@RequestParam("file") MultipartFile file) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        return songService.getSingleFileInfo(file);
    }

    @PostMapping("/multipleFileInfo")
    @ResponseBody
    public ArrayList<HashMap<String,String>> getMultipleFileInfo(@RequestParam("files") List<MultipartFile> files) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        for(MultipartFile file : files){
            list.add(songService.getSingleFileInfo(file));
        }
        return list;
    }
}
