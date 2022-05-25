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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.File;
import java.util.*;

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
    public Optional<Song> addSong(@NotNull String title, @Null Set<String> artists, @NotNull MultipartFile file) {
        if(title.isEmpty()) {
            //log
            return Optional.empty();
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

            Song createdSong;
            if(artists != null)
            {
                Set<Artist> artistSet = createArtistsFromStrings(artists);
                // create Song instance and add it to repository
                createdSong = songRepository.save(new Song(title, artistSet, location));
            } else
                // create Song instance and add it to repository
                createdSong = songRepository.save(new Song(title, location));
            return Optional.of(createdSong);
        }
        return Optional.empty();
    }

    @Override
    public Set<Artist> createArtistsFromStrings(Set<String> artists) {
        Set<Artist> artistSet = new HashSet<>();
        for (String artist : artists) {
            if(artistRepository.existsByName(artist)){
                artistSet.add(artistRepository.findByName(artist).iterator().next());
            }else
            {
                Artist curArtist = artistRepository.save(new Artist(artist));
                artistSet.add(curArtist);
            }
        }
        return artistSet;
    }

    @Override
    public boolean updateSong(@NotNull Long id, @NotNull String title, @NotNull Set<String> artists) {
        try {
            Song song = songRepository.findById(id).orElseThrow();
            song.setTitle(title);
            song.setArtists(createArtistsFromStrings(artists));
            songRepository.save(song);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean songExistsById(Long id)
    {
        return songRepository.existsById(id);
    }

    @Override
    public List<Long> removeNonExistentIds(List<Long> ids) {
        List<Long> existingIds = new ArrayList<>();
        for(Long id : ids)
            if(songRepository.existsById(id))
                existingIds.add(id);
        return existingIds;
    }

    @Override
    public String idListToString(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for(Long id : ids)
            sb.append(id.toString()+',');
        return sb.substring(0, sb.length() - 1);
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
    public void deleteAll() {
        songRepository.deleteAll();
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
