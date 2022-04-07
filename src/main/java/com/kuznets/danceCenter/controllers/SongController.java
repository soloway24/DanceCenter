package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    public RedirectView addSong(@RequestParam String name, @RequestParam(required = false) Set<String> artists,
                                @RequestParam MultipartFile file , RedirectAttributes redir){

        RedirectView redirectView = new RedirectView("/",true);

        boolean success = songService.addSong(name, artists, file);
        String notification;
        if (success)
            notification = "Композиція '" + name + "' була успішно додана.";
        else
            notification = "Композиція '" + name + " не була додана.";
// log
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }


    @PostMapping("/delete")
    public RedirectView deleteSong(@RequestParam Long id, RedirectAttributes redir) throws Exception {
        RedirectView redirectView = new RedirectView("/",true);
        String name = songService.getSongById(id).getName();
        String notification = "Композиція '"+ name +"' була успішно видалена!";
        boolean success =  songService.deleteSong(id);
//logging
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        return redirectView;
    }


    @PostMapping("/fileInfo")
    @ResponseBody
    public HashMap<String,String> getFileInfo(@RequestParam("file") MultipartFile file){
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
