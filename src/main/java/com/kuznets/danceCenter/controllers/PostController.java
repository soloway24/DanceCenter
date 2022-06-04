package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.implementations.UserDetailsServiceImpl;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@Transactional
@RequestMapping("/posts")
public class PostController {

    private final PostServiceInterface postService;
    private final SongServiceInterface songService;
    private final UserServiceInterface userService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public PostController(PostServiceInterface postService, SongServiceInterface songService,
                          UserServiceInterface userService, UserDetailsServiceImpl userDetailsService) {
        this.postService = postService;
        this.songService = songService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    private void populateModel(Model model) {
        model.addAttribute("posts", postService.getAll());
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
    }

    @GetMapping
    public String posts(Model model) {
        populateModel(model);
        return "posts";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        AppUser currentUser = userDetailsService.getCurrentAppUser();
        ArrayList<Post> posts = new ArrayList<>();
        for(AppUser foll : currentUser.getFollowing()) {
            posts.addAll(foll.getPosts());
        }
        model.addAttribute("posts", posts);
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        return "posts";
    }

    @GetMapping("/{username}")
    public String postsOfUser(@PathVariable("username") String username, Model model) {
        userDetailsService.addUserToModel(model);
        AppUser viewedUser = userService.getUserByUsername(username);
        model.addAttribute("posts", viewedUser.getPosts());
        Utils.addAppNameToModel(model);
        return "posts";
    }

    @GetMapping("/add")
    public String addPostPage(Model model, @RequestParam(name = "postSongsIds", required = false) String postSongsIds,
                              @RequestParam(name = "description", required = false) String description, RedirectAttributes redir){

        if(postSongsIds != null){
            List<Long> ids = Arrays.stream(postSongsIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<Long> existingIds = songService.removeNonExistentIds(ids);
            if(existingIds.size() == 0) {
                if(description != null)
                    return "redirect:/posts/add?description="+description;
                else return "redirect:/posts/add";
            }
            if(ids.size() == existingIds.size()){
                ArrayList<Song> postSongs = null;
                try {
                    postSongs = (ArrayList<Song>) songService.getSongsByIds(ids);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(description != null){
                    model.addAttribute("description", description);
                }
                model.addAttribute("postSongs", postSongs);
            }else{
                String urlIds = Utils.idListToString(existingIds);
                System.out.println(urlIds);
                if(description != null){
                    return "redirect:/posts/add?postSongsIds="+urlIds+"&description="+description;
                }
                else return "redirect:/posts/add?postSongsIds="+urlIds;
            }
        }else {
            if(description != null){
                model.addAttribute("description", description);
            }
        }
        populateModel(model);
        return "addPostPage";
    }

    @PostMapping("/add")
    public RedirectView addPost(@RequestParam String description, @RequestParam("songIds") String songsIdsUnparsed, RedirectAttributes redir){
        List<Long> ids = songsIdsUnparsed.length()>2 ? Utils.stringToIdList(songsIdsUnparsed) : new ArrayList<>();
        if(ids.size() == 0)
            return new RedirectView("/errors/addPost",true);

        boolean success;
        String notification;
        AppUser currentUser = userDetailsService.getCurrentAppUser();
        try {
            List<Song> songs = songService.getSongsByIds(ids);
            postService.addPost(description, songs, currentUser);
            notification = "Post '"+description+"' has been added!";
            success = true;
        } catch (Exception e) {
            notification = "Post '"+description+"' has not been added!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        if(success){
            return new RedirectView("/users/"+currentUser.getUsername()+"/profile",true);
        }else
            return new RedirectView("/errors/addPost",true);
    }


    @PostMapping("/delete")
    public RedirectView deletePost(@RequestParam Long id, HttpServletRequest request, RedirectAttributes redir) {
        boolean success;
        String notification;

        try {
            postService.deletePostById(id);
            notification = "Публікація '"+ id +"' була успішно видалена!";
            success = true;
        } catch (Exception e) {
            notification = "Публікація '"+ id +"' не була видалена!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        if(success){
            String referer = request.getHeader("Referer");
            return new RedirectView(referer,true);
        }else
            return new RedirectView("/errors/error",true);
    }
}
