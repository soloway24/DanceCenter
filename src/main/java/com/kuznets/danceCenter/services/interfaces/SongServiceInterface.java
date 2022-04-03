package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


public interface SongServiceInterface {

    boolean addSong(String name, Set<String> artists, MultipartFile file);
//    Song addSong(Song song);

    boolean songExistsById(Long id);

    boolean deleteSong(Long id);

//    boolean updateSong(Long id, String newName);
//    boolean updateSong(Long id, String newName, Set<artist> artists);
//    Teacher updateTeacher(Teacher teacher);

    Song getSongById(Long id) throws Exception;
    Iterable<Song> getAll();
}
