package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Post;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.SongServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/posts")
public class PostController {

    private PostServiceInterface postService;
    private SongServiceInterface songService;
    private UserServiceInterface userService;

    @Autowired
    public PostController(PostServiceInterface postService, SongServiceInterface songService,
                          UserServiceInterface userService) {
        this.postService = postService;
        this.songService = songService;
        this.userService = userService;
    }

    private void populateModel(Model model) {
        model.addAttribute("posts", postService.getAll());
        model.addAttribute("currentUser", userService.getCurrentUser());
    }

    private void addUserToModel(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
    }

    @GetMapping
    public String posts(Model model) {
        populateModel(model);
        return "posts";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        AppUser currentUser = userService.getCurrentUser();
        ArrayList<Post> posts = new ArrayList<>();
        for(AppUser foll : currentUser.getFollowing()) {
            posts.addAll(foll.getPosts());
        }
        model.addAttribute("posts", posts);
        addUserToModel(model);
        return "posts";
    }

    @GetMapping("/{username}")
    public String postsOfUser(@PathVariable("username") String username, Model model) {
        addUserToModel(model);
        AppUser viewedUser = userService.getUserByUsername(username);
        model.addAttribute("posts", viewedUser.getPosts());
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
        try {
            List<Song> songs = songService.getSongsByIds(ids);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            AppUser appUser = userService.getUserByUsername(user.getUsername());
            postService.addPost(description, songs, appUser);
            notification = "Публікація '"+description+"' була успішно додана!";
            success = true;
        } catch (Exception e) {
            notification = "Публікація '"+description+"' не була додана!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        if(success){
            return new RedirectView("/",true);
        }else
            return new RedirectView("/errors/addPost",true);
    }


    @PostMapping("/delete")
    public RedirectView deletePost(@RequestParam Long id, RedirectAttributes redir) {
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
            return new RedirectView("/",true);
        }else
            return new RedirectView("/errors/error",true);
    }
}
