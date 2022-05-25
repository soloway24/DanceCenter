package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
    public RedirectView addSong(@RequestParam String title, @RequestParam(required = false) Set<String> artists,
                                @RequestParam MultipartFile file , RedirectAttributes redir, Model model){

        RedirectView redirectView = new RedirectView("/addPost",true);

        ArrayList<Song> songs = new ArrayList<>();
        String notification;
        boolean success;
        try {
            songs.add(songService.addSong(title, artists, file).orElseThrow());
            notification = "Композиція '" + title + "' була успішно додана.";
            success = true;
            redir.addFlashAttribute("postSongs", songs);
        } catch (Exception e) {
            notification = "Композиція '" + title + " не була додана.";
            success = false;
        }

// log
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }

    @PostMapping("/multipleAdd")
    @ResponseBody
    public List<Long> addMultipleSongs(@RequestParam(name = "titles") String titlesUnparsed, @RequestParam(name = "artists", required = false) String artistsUnparsed,
                                         @RequestParam(name = "files") MultipartFile[] files , RedirectAttributes redir){
        JSONArray titlesJson = new JSONArray(titlesUnparsed);

        JSONArray artistListsJson = new JSONArray(artistsUnparsed);
        ArrayList<Set<String>> artists = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        int nFiles = titlesJson.length();


        for (int i = 0; i < nFiles; i++) {
            titles.add(titlesJson.get(i).toString());

            JSONArray currentArtistsJson = new JSONArray(artistListsJson.get(i).toString());
            Set<String> currentArtists = new HashSet<>();
            for(int j = 0; j < currentArtistsJson.length(); j++)
                currentArtists.add(currentArtistsJson.get(j).toString());
            artists.add(currentArtists);
        }

        RedirectView redirectView = new RedirectView("/addPost",true);
        boolean[] successes = new boolean[nFiles];
        ArrayList<Song> songs = new ArrayList<>();
        String[] notifications = new String[nFiles];

        for(int i = 0; i < nFiles; i++) {
            try {
                songs.add(songService.addSong(titles.get(i), artists.get(i), files[i]).orElseThrow());
                notifications[i] = "Композиція '" + titles.get(i) + "' була успішно додана.";
                successes[i] = true;
            } catch (Exception e) {
                e.printStackTrace();
                notifications[i] = "Композиція '" + titles.get(i) + " не була додана.";
                successes[i] = false;
            }
        }
        return songs.stream().map(Song::getId).collect(Collectors.toList());
// log
//        redir.addFlashAttribute("successes", successes);
//        redir.addFlashAttribute("postSongs", songs);
//        redir.addFlashAttribute("notifications", notifications);
//        return redirectView;
    }

    @PostMapping("/delete")
    public RedirectView deleteSong(@RequestParam Long id, HttpServletRequest request, RedirectAttributes redir) throws Exception {
        String referer = request.getHeader("Referer");

        String title = songService.getSongById(id).getTitle();
        String notification = "Композиція '"+ title +"' була успішно видалена!";
        boolean success =  songService.deleteSong(id);
//logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return new RedirectView(referer,true);
    }


    @PostMapping("/fileInfo")
    @ResponseBody
    public HashMap<String,String> getFileInfo(@RequestParam("file") MultipartFile file){
        return getSingleFileInfo(file);
    }

    @PostMapping("/multipleFileInfo")
    @ResponseBody
    public ArrayList<HashMap<String,String>> getMultipleFileInfo(@RequestParam("files") List<MultipartFile> files){
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        for(MultipartFile file : files){
            list.add(getSingleFileInfo(file));
        }
        return list;
    }

    private HashMap<String,String> getSingleFileInfo(MultipartFile file) {
        AudioFile audioFile = null;
        HashMap<String, String > map = new HashMap<>();

        try {
            File uploadPath = new File(Values.UPLOAD_PATH);
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "." + file.getOriginalFilename();
            File newFile = new File(uploadPath.getAbsolutePath(), fileName);

            try {
                file.transferTo(newFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            audioFile = AudioFileIO.read(newFile);
            Tag tag = audioFile.getTag();
            map.put("TITLE", tag.getFirst(FieldKey.TITLE));
            map.put("ARTIST", tag.getFirst(FieldKey.ARTIST));
            map.put("ALBUM_ARTIST", tag.getFirst(FieldKey.ALBUM_ARTIST));
            map.put("ALBUM", tag.getFirst(FieldKey.ALBUM));
            map.put("YEAR", tag.getFirst(FieldKey.YEAR));
            map.put("COMPOSER", tag.getFirst(FieldKey.COMPOSER));
            map.put("GENRE", tag.getFirst(FieldKey.GENRE));
            newFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
