package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SongServiceInterface {
    Song addSong(String title, List<String> artists, MultipartFile file) throws Exception;

    Song updateSong(Long id, String title, List<String> artists) throws Exception;

    boolean songExistsById(Long id);
    Song getSongById(Long id) throws Exception;
    List<Song> getSongsByIds(List<Long> ids) throws Exception;
    Iterable<Song> getAll();

    void deleteSongById(Long id) throws Exception;
    void deleteSongsByIds(List<Long> ids) throws Exception;
    void deleteAll();

    List<Long> removeNonExistentIds(List<Long> ids);
    List<Artist> createArtistsFromStrings(List<String> artists);
}
