package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;

import java.util.List;

public interface PostServiceInterface {
    Post addPost(String description, List<Song> songs, AppUser appUser);

    boolean postExistsById(Long id);
    Post getPostById(Long id) throws Exception;
    Iterable<Post> getAll();

    void deletePostById(Long id) throws Exception;

    List<Post> sortPosts(List<Post> posts);
}
