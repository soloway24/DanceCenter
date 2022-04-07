package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.SongNotFoundException;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.ArtistRepository;
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
import javax.validation.constraints.Null;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SongService implements SongServiceInterface {

    private SongRepository songRepository;
    private ArtistRepository artistRepository;


    private static Logger logger = LogManager.getLogger(SongService.class);

    @Autowired
    public void setTeacherRepository(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public boolean addSong(@NotNull String title, @Null Set<String> artists, @NotNull MultipartFile file) {
        if(title.isEmpty()) {
            //log
            return false;
        }
        if(artists != null && artists.isEmpty())
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

            // create unique title for file using uuid to avoid collisions
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "." + file.getOriginalFilename();
            fileName = fileName.replaceAll(" ", "_");
            File newFile = new File(uploadPath.getAbsolutePath(), fileName);

            // write multipart file to file on disk
            try {
                file.transferTo(newFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String location = Values.BEGIN_FILE_LOCATION + fileName;

            if(artists != null)
            {
                Set<Artist> artistSet = new HashSet<>();
                for (String artist : artists) {
                    if(artistRepository.existsByName(artist)){
                        artistSet.add(artistRepository.findByName(artist).iterator().next());
                    }else
                    {
                        artistSet.add(new Artist(artist));
                    }
                }
                // create Song instance and add it to repository
                songRepository.save(new Song(title, artistSet, location));
            } else
                // create Song instance and add it to repository
                songRepository.save(new Song(title, location));
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
        try {

            File uploadPath = new File(Values.UPLOAD_PATH);
            if(!uploadPath.exists())
                throw new Exception("Upload folder doesn't exist.");

            File file = new File(uploadPath.getAbsolutePath() + "/" + getSongById(id).getLocation().substring(6));
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
