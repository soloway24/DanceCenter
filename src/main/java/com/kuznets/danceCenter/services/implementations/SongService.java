package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.SongNotFoundException;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.SongRepository;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SongService implements SongServiceInterface {

    private SongRepository songRepository;
    private static Logger logger = LogManager.getLogger(SongService.class);

    @Autowired
    public void setTeacherRepository(SongRepository songRepository) {
        this.songRepository = songRepository;
    }


    @Override
    public boolean addSong(String name, Set<Artist> artists) {
        songRepository.save(new Song(name, artists));
        return true;
    }

    @Override
    public boolean songExistsById(Long id)
    {
        return songRepository.existsById(id);
    }

    @Override
    public boolean deleteSong(Long id) {
        if(!songExistsById(id)) throw new SongNotFoundException(id);
        songRepository.deleteById(id);
        return true;
    }

    @Override
    public Song getSongById(Long id) throws Exception {
        return songRepository.findById(id).orElseThrow(() -> new SongNotFoundException(id));
    }

    @Override
    public Iterable<Song> getAll() {
        return songRepository.findAll();
    }
}
