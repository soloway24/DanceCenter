package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import com.kuznets.danceCenter.utils.Values;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/songs")
public class SongController {

    private SongServiceInterface songService;

    @Autowired
    public SongController(SongServiceInterface songService) {
        this.songService = songService;
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
            for(int j = 0; j < currentArtistsJson.length(); j++)
                currentArtists.add(currentArtistsJson.get(j).toString());
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
            notification = "Композиція (id=" + id + ") була успішно оновлена.";
            success = true;
        } catch (Exception e) {
            notification = "Композиція (id=" + id + ") не була оновлена.";
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
            notification = "Композиція '"+ title +"'(id=" + id + ") була успішно видалена!";
            success = true;
        } catch (Exception e) {
            notification = "Композиція (id=" + id + ") не була видалена!";
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
            return new RedirectView("/errors/delete",true);

        boolean success;
        String notification;
        try {
            songService.deleteSongsByIds(ids);
            notification = "Композиції із ID: '"+ Arrays.toString(ids.toArray()) +"' були успішно видалені!";
            success = true;
        } catch (Exception e) {
            notification = "(Не всі) Композиції із ID: '"+ Arrays.toString(ids.toArray()) +"' не були видалені!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);

        if(success){
            String referer = request.getHeader("Referer");
            return new RedirectView(referer,true);
        }else
            return new RedirectView("/errors/delete",true);
    }

    @PostMapping("/deleteAll")
    public RedirectView deleteAllSongs(HttpServletRequest request, RedirectAttributes redir){

        songService.deleteAll();

        String notification = "Усі композиції були успішно видалені!";

        redir.addFlashAttribute("success", true);
        redir.addFlashAttribute("notification", notification);

        String referer = request.getHeader("Referer");
        return new RedirectView(referer,true);
    }

    @PostMapping("/fileInfo")
    @ResponseBody
    public HashMap<String,String> getFileInfo(@RequestParam("file") MultipartFile file) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        return getSingleFileInfo(file);
    }

    @PostMapping("/multipleFileInfo")
    @ResponseBody
    public ArrayList<HashMap<String,String>> getMultipleFileInfo(@RequestParam("files") List<MultipartFile> files) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        for(MultipartFile file : files){
            list.add(getSingleFileInfo(file));
        }
        return list;
    }

    private HashMap<String,String> getSingleFileInfo(MultipartFile file) throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
        AudioFile audioFile;
        HashMap<String, String > map = new HashMap<>();

        File uploadPath = new File(Values.UPLOAD_PATH);
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + file.getOriginalFilename();
        File newFile = new File(uploadPath.getAbsolutePath(), fileName);

        file.transferTo(newFile);
        audioFile = AudioFileIO.read(newFile);

        Tag tag = audioFile.getTag();
        if(tag != null) {
            map.put("TITLE", tag.getFirst(FieldKey.TITLE));
            map.put("ARTIST", tag.getFirst(FieldKey.ARTIST));
            map.put("ALBUM_ARTIST", tag.getFirst(FieldKey.ALBUM_ARTIST));
            map.put("ALBUM", tag.getFirst(FieldKey.ALBUM));
            map.put("YEAR", tag.getFirst(FieldKey.YEAR));
            map.put("COMPOSER", tag.getFirst(FieldKey.COMPOSER));
            map.put("GENRE", tag.getFirst(FieldKey.GENRE));
        } else {
            map.put("TITLE", "");
            map.put("ARTIST", "");
            map.put("ALBUM_ARTIST", "");
            map.put("ALBUM", "");
            map.put("YEAR", "");
            map.put("COMPOSER", "");
            map.put("GENRE", "");
        }


        newFile.delete();

        return map;
    }
}
