package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface SongServiceInterface {

    Optional<Song> addSong(String title, Set<String> artists, MultipartFile file);
    boolean updateSong(Long id, String title, Set<String> artists);

    boolean songExistsById(Long id);
    boolean deleteSong(Long id);

    boolean deleteSongsByIds(List<Long> ids);

    void deleteAll();

    List<Long> removeNonExistentIds(List<Long> ids);
    Set<Artist> createArtistsFromStrings(Set<String> artists);

//    boolean updateSong(Long id, String newName);
//    boolean updateSong(Long id, String newName, Set<artist> artists);
//    Teacher updateTeacher(Teacher teacher);

    Song getSongById(Long id) throws Exception;
    Iterable<Song> getAll();
}
