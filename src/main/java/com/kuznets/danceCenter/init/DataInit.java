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
        //initialize();
    }


    private void initialize() {


    }

}
