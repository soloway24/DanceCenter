package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.exceptions.PostNotFoundException;
import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.repositories.PostRepository;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService implements PostServiceInterface {

    private PostRepository postRepository;

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post addPost(@NotNull String description, @NotNull List<Song> songs, @NotNull AppUser user) {
        return postRepository.save(new Post(description, songs, user));
    }

    @Override
    public boolean postExistsById(Long id) {
        return postRepository.existsById(id);
    }

    @Override
    public Post getPostById(Long id){
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Override
    public Iterable<Post> getAll() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @Override
    public void deletePostById(Long id) throws PostNotFoundException{
        if(!postExistsById(id)) throw new PostNotFoundException(id);
        postRepository.deleteById(id);
    }

    public List<Post> sortPosts(List<Post> posts) {
        return postRepository.findByIdIn(posts.stream().map(Post::getId).collect(Collectors.toList()),
                Sort.by(Sort.Direction.DESC, "date"));
    }


}
