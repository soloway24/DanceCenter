package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.SongNotFoundException;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.SongRepository;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Set;
import java.util.UUID;

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
    public boolean addSong(@NotNull String name, @NotNull Set<Artist> artists, @NotNull MultipartFile file) {
        if(name.isEmpty()) {
            //log
            return false;
        }
        if(artists.isEmpty())
        {
            //log
            return false;
        }
        if(!file.getOriginalFilename().isEmpty())
        {
            // create file upload directory if it doesn't exist
            File uploadPath = new File(Values.UPLOAD_PATH);
            if(!uploadPath.exists())
                uploadPath.mkdir();

            // create unique name for file using uuid to avoid collisions
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "." + file.getOriginalFilename();
            File newFile = new File(uploadPath.getAbsolutePath(), fileName);

            // write multipart file to file on disk
            try {
                file.transferTo(newFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // create Song instance and add it to repository
            String location = Values.BEGIN_FILE_LOCATION + fileName;
            songRepository.save(new Song(name, artists, location));
            return true;
        }
        return false;
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
