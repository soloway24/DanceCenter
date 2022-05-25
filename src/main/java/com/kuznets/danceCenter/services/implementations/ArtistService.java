package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.ArtistRepository;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ArtistService implements ArtistServiceInterface {

    private ArtistRepository artistRepository;
    private static Logger logger = LogManager.getLogger(ArtistService.class);

    @Autowired
    public void setTeacherRepository(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }


    @Override
    public boolean addArtist(String name, Set<Song> songs) {
        artistRepository.save(new Artist(name, songs));
        return true;
    }

    @Override
    public boolean artistExistsById(Long id) {
        return artistRepository.existsById(id);
    }

    @Override
    public boolean deleteArtist(Long id) {
        if(artistExistsById(id)) {
            artistRepository.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    public Artist getArtistById(Long id) throws Exception {
        return artistRepository.findById(id).orElseThrow(() -> new Exception("No artist with such id: " + id));
    }

    @Override
    public Iterable<Artist> getAll() {
        return artistRepository.findAll();
    }
}
