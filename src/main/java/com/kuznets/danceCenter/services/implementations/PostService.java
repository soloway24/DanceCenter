package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.PostNotFoundException;
import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.PostRepository;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PostService implements PostServiceInterface {

    private PostRepository postRepository;
    private static Logger logger = LogManager.getLogger(PostService.class);

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public boolean addPost(String description, Set<Song> songs) {
        postRepository.save(new Post(description, songs));
        return true;
    }

    @Override
    public boolean postExistsById(Long id) {
        return postRepository.existsById(id);
    }

    @Override
    public boolean deletePost(Long id) {
        if(!postExistsById(id)) throw new PostNotFoundException(id);
        postRepository.deleteById(id);
        return true;
    }

    @Override
    public Post getPostById(Long id) throws Exception {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Override
    public Iterable<Post> getAll() {
        return postRepository.findAll();
    }
}
