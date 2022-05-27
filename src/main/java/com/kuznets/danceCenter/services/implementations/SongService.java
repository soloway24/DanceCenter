package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.SongNotFoundException;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.ArtistRepository;
import com.kuznets.danceCenter.repositories.SongRepository;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class SongService implements SongServiceInterface {

    private SongRepository songRepository;
    private ArtistRepository artistRepository;


    @Autowired
    public void setRepositories(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }


    @Override
    public Song addSong(@NotNull String title, List<String> artists, @NotNull MultipartFile file) throws IOException, ResponseStatusException {
        if(file.getOriginalFilename().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add a song with an empty audio file.");
        String songTitle = "";
        if(title.isEmpty())
            songTitle = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        else
            songTitle = title;

        Song createdSong;
        String location = "";
        if(artists != null) {
            List<Artist> artistSet = createArtistsFromStrings(artists);
            createdSong = songRepository.save(new Song(songTitle, artistSet, location));
        } else
            createdSong = songRepository.save(new Song(songTitle, location));

        // create file upload directory if it doesn't exist
        File uploadPath = new File(Values.UPLOAD_PATH);
        if(!uploadPath.exists())
            uploadPath.mkdir();

        // create unique title for file using uuid to avoid collisions
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + createdSong.getId();
//        fileName = fileName.replaceAll(" ", "_");
        File newFile = new File(uploadPath.getAbsolutePath(), fileName);

        // write multipart file to file on disk
        file.transferTo(newFile);
        location = Values.BEGIN_FILE_LOCATION + fileName;
        createdSong.setLocation(location);
        return songRepository.save(createdSong);
    }

    @Override
    public List<Artist> createArtistsFromStrings(List<String> artists) {
        List<Artist> artistList = new ArrayList<>();
        if(artists == null)
            return artistList;
        for (String artist : artists) {
            if(artistRepository.existsByName(artist)){
                artistList.add(artistRepository.findByName(artist).iterator().next());
            }else
            {
                Artist curArtist = artistRepository.save(new Artist(artist));
                artistList.add(curArtist);
            }
        }
        return artistList;
    }

    @Override
    public Song updateSong(@NotNull Long id, @NotNull String title, List<String> artists) throws SongNotFoundException {
        Song song = getSongById(id);
        song.setTitle(title);
        song.setArtists(createArtistsFromStrings(artists));
        return songRepository.save(song);
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
    public boolean songExistsById(Long id) {
        return songRepository.existsById(id);
    }

    @Override
    public Song getSongById(Long id) throws SongNotFoundException {
        return songRepository.findById(id).orElseThrow(() -> new SongNotFoundException(id));
    }

    @Override
    public List<Song> getSongsByIds(List<Long> ids) throws Exception {
        List<Song> songs = new ArrayList<>();
        for(Long id : ids)
            songs.add(getSongById(id));
        return songs;
    }

    @Override
    public Iterable<Song> getAll() {
        return songRepository.findAll();
    }

    @Override
    public void deleteSongById(Long id) throws Exception {
        if(!songExistsById(id)) throw new SongNotFoundException(id);

        File uploadPath = new File(Values.UPLOAD_PATH);
        if(!uploadPath.exists())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Upload folder doesn't exist.");

        File file = new File(uploadPath.getAbsolutePath() + "/"
                + getSongById(id).getLocation().substring(Values.BEGIN_FILE_LOCATION.length()));
        boolean fileDeleted = file.delete();

        if(!fileDeleted)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"File was not deleted.");

        songRepository.deleteById(id);
    }

    @Override
    public void deleteSongsByIds(List<Long> ids) throws Exception {
        for(Long id : ids)
            deleteSongById(id);
    }

    @Override
    public void deleteAll() {
        songRepository.deleteAll();
    }
}
