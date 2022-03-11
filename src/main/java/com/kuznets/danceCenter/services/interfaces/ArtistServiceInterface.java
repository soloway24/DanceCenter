package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;

import java.util.Set;


public interface ArtistServiceInterface {

    boolean addArtist(String name, Set<Song> songs);
//    Song addArtist(Artist artist);

    boolean artistExistsById(Long id);

    boolean deleteArtist(Long id);

//    boolean updateArtist(Long id, String newName);
//    boolean updateSong(Long id, String newName, Set<Artist> artists);
//    Teacher updateTeacher(Teacher teacher);

    Artist getArtistById(Long id) throws Exception;
    Iterable<Artist> getAll();
}
