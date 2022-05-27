package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;

import java.util.List;

public interface ArtistServiceInterface {

    Artist addArtist(String name, List<Song> songs);

    boolean artistExistsById(Long id);
    Artist getArtistById(Long id) throws Exception;
    Iterable<Artist> getAll();

    boolean deleteArtist(Long id);
}
