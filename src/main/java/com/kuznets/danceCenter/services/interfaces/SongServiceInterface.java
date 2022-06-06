package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public interface SongServiceInterface {
    Song addSong(String title, List<String> artists, MultipartFile file) throws Exception;

    Song updateSong(Long id, String title, List<String> artists) throws Exception;

    boolean songExistsById(Long id);
    Song getSongById(Long id) throws Exception;
    List<Song> getSongsByIds(List<Long> ids) throws Exception;
    List<Song> getAll();
    List<Song> getBySearchText(String searchQuery);

    void deleteSongById(Long id) throws Exception;
    void deleteSongsByIds(List<Long> ids) throws Exception;
    void deleteAll();

    List<Long> removeNonExistentIds(List<Long> ids);
    List<Artist> createArtistsFromStrings(List<String> artists);

    HashMap<String, String> getSingleFileInfo(MultipartFile file) throws IOException, CannotReadException,
            TagException, InvalidAudioFrameException, ReadOnlyFileException;

    List<Song> sortSongs(List<Song> songs);
}
