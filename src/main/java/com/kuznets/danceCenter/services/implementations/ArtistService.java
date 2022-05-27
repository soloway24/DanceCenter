package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.ArtistNotFoundException;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.ArtistRepository;
import com.kuznets.danceCenter.services.interfaces.ArtistServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class ArtistService implements ArtistServiceInterface {

    private ArtistRepository artistRepository;

    @Autowired
    public void setArtistRepository(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }


    @Override
    public Artist addArtist(@NotNull String name, @NotNull List<Song> songs) {
        if(name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add artists with an empty name.");
        return artistRepository.save(new Artist(name, songs));
    }

    @Override
    public boolean artistExistsById(Long id) {
        return artistRepository.existsById(id);
    }

    @Override
    public Artist getArtistById(Long id) {
        return artistRepository.findById(id).orElseThrow(() -> new ArtistNotFoundException(id));
    }

    @Override
    public Iterable<Artist> getAll() {
        return artistRepository.findAll();
    }

    @Override
    public boolean deleteArtist(Long id) {
        if(!artistExistsById(id)) throw new ArtistNotFoundException(id);

        artistRepository.deleteById(id);
        return true;
    }
}
