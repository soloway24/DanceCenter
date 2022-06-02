package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.SongNotFoundException;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.ArtistRepository;
import com.kuznets.danceCenter.repositories.SongRepository;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.utils.Values;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
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
        List<Artist> oldArtists = song.getArtists();
        song.setTitle(title);
        song.setArtists(createArtistsFromStrings(artists));
        for(Artist artist : oldArtists){
            if(!song.getArtists().contains(artist) && artist.getSongs().size() == 1)
                artistRepository.delete(artist);
        }
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

        Song song = songRepository.getById(id);

        File uploadPath = new File(Values.UPLOAD_PATH);
        if(!uploadPath.exists())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Upload folder doesn't exist.");

        File file = new File(uploadPath.getAbsolutePath() + "/"
                + getSongById(id).getLocation().substring(Values.BEGIN_FILE_LOCATION.length()));
        boolean fileDeleted = file.delete();

        if(!fileDeleted)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"File was not deleted.");

        songRepository.deleteById(id);

        for(Artist artist : song.getArtists())
            if(artist.getSongs().size()==1)
                artistRepository.delete(artist);
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

    public HashMap<String,String> getSingleFileInfo(MultipartFile file) throws IOException, CannotReadException,
            TagException, InvalidAudioFrameException, ReadOnlyFileException {
        AudioFile audioFile;
        HashMap<String, String > map = new HashMap<>();

        File uploadPath = new File(Values.UPLOAD_PATH);
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + file.getOriginalFilename();
        File newFile = new File(uploadPath.getAbsolutePath(), fileName);

        file.transferTo(newFile);
        audioFile = AudioFileIO.read(newFile);

        Tag tag = audioFile.getTag();
        if(tag != null) {
            map.put("TITLE", tag.getFirst(FieldKey.TITLE));
            map.put("ARTIST", tag.getFirst(FieldKey.ARTIST));
            map.put("ALBUM_ARTIST", tag.getFirst(FieldKey.ALBUM_ARTIST));
            map.put("ALBUM", tag.getFirst(FieldKey.ALBUM));
            map.put("YEAR", tag.getFirst(FieldKey.YEAR));
            map.put("COMPOSER", tag.getFirst(FieldKey.COMPOSER));
            map.put("GENRE", tag.getFirst(FieldKey.GENRE));
        } else {
            map.put("TITLE", "");
            map.put("ARTIST", "");
            map.put("ALBUM_ARTIST", "");
            map.put("ALBUM", "");
            map.put("YEAR", "");
            map.put("COMPOSER", "");
            map.put("GENRE", "");
        }

        newFile.delete();

        return map;
    }
}
