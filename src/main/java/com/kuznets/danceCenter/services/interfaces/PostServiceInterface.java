package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;

import java.util.Set;


public interface PostServiceInterface {

    boolean addPost(String name, Set<Song> songs);
//    Post addPost(Post post);

    boolean postExistsById(Long id);

    boolean deletePost(Long id);

//    boolean updatePost(Long id, String newName);
//    boolean updatePost(Long id, String newName, Set<artist> artists);
//    Teacher updateTeacher(Teacher teacher);

    Post getPostById(Long id) throws Exception;
    Iterable<Post> getAll();
}
