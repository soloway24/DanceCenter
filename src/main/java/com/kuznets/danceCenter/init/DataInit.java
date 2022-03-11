package com.kuznets.danceCenter.init;

import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.ArtistRepository;
import com.kuznets.danceCenter.repositories.PostRepository;
import com.kuznets.danceCenter.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements ApplicationRunner {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }


    private void initialize() {

        Post post1 = new Post("Post 1");
        Post post2 = new Post("Post 2");
        Post post3 = new Post("Post 3");

        Song song1 = new Song("Song 1");
        Song song2 = new Song("Song 2");
        Song song3 = new Song("Song 3");

        Artist artist1 = new Artist("Artist 1");
        Artist artist2 = new Artist("Artist 2");


        song1.addArtist(artist1);
        song1.addArtist(artist2);
        song2.addArtist(artist1);
        song3.addArtist(artist2);

        song1.setLocation("song1");
        song2.setLocation("song2");
        song3.setLocation("song3");

        post1.addSong(song1);
        post1.addSong(song2);
        post2.addSong(song2);
        post2.addSong(song3);
        post3.addSong(song1);
        post3.addSong(song3);

        songRepository.save(song1);
        songRepository.save(song2);
        songRepository.save(song3);

        artistRepository.save(artist1);
        artistRepository.save(artist2);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

    }

}
