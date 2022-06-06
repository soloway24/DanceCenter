package com.kuznets.danceCenter.controllers;


import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.services.implementations.UserDetailsServiceImpl;
import com.kuznets.danceCenter.services.interfaces.PostServiceInterface;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.Access;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/users")
public class UserController {

    private final UserServiceInterface userService;
    private final PostServiceInterface postService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserController(UserServiceInterface userService,
                          PostServiceInterface postService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.postService = postService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/getList")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }


    @GetMapping
    public String users(Model model) {
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        model.addAttribute("users",userService.getAllUsers());
        return "users";
    }

    @GetMapping("/{username}/profile")
    public String profile(@PathVariable("username") String username, Model model, RedirectAttributes redir) {
        if(!username.equals(userDetailsService.getCurrentAppUser().getUsername())){
            redir.addFlashAttribute("notification", "No permission to open other user's profile.");
            return "redirect:/errors/error";
        }
        userDetailsService.addUserToModel(model);
        model.addAttribute("userPosts", postService.sortPosts(userDetailsService.getCurrentAppUser().getPosts()));
        Utils.addAppNameToModel(model);
        return "profile";
    }

    @GetMapping("/{username}")
    public String user(@PathVariable("username") String username, Model model) {
        if(username.equals(userDetailsService.getCurrentAppUser().getUsername())){
            return "redirect:/users/"+username+"/profile";
        }
        AppUser viewedUser = userService.getUserByUsername(username);
        userDetailsService.addUserToModel(model);
        model.addAttribute("users", Collections.singleton(viewedUser));
        model.addAttribute("userPosts", postService.sortPosts(viewedUser.getPosts()));
        model.addAttribute("showPosts", true);
        Utils.addAppNameToModel(model);
        return "users";
    }

    @GetMapping("/{username}/followers")
    public String userFollowers(@PathVariable("username") String username, Model model) {
        AppUser viewedUser = userService.getUserByUsername(username);
        model.addAttribute("users",  userService.sortUsers(viewedUser.getFollowers()));
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        return "users";
    }

    @GetMapping("/{username}/following")
    public String userFollowing(@PathVariable("username") String username, Model model) {
        AppUser viewedUser = userService.getUserByUsername(username);
        model.addAttribute("users", userService.sortUsers(viewedUser.getFollowing()));
        userDetailsService.addUserToModel(model);
        Utils.addAppNameToModel(model);
        return "users";
    }

    @PostMapping("/{username}/toggleFollow")
    public RedirectView userToggleFollow(@PathVariable("username") String username, HttpServletRequest request,
                                         RedirectAttributes redir) {
        boolean success;
        String notification;
        try {
            AppUser appUser = userService.getUserByUsername(username);
            AppUser currentUser = userDetailsService.getCurrentAppUser();
            if (currentUser.getFollowing().contains(appUser))
                currentUser.getFollowing().remove(appUser);
            else
                currentUser.getFollowing().add(appUser);
            userService.saveUser(currentUser);
            notification = "User '"+username+"' is now followed by You.";
            success = true;
        } catch (Exception e) {
            notification = "User '"+username+"' is not followed.";
            success = false;
        }
        redir.addFlashAttribute("notification", notification);
        redir.addFlashAttribute("success", success);
        if(success){
            String referer = request.getHeader("Referer");
            return new RedirectView(referer,true);
        }else
            return new RedirectView("/errors/follow",true);
    }

    @PostMapping("/add")
    public RedirectView addUser(@RequestParam String username, @RequestParam String password,
                                HttpServletRequest request, RedirectAttributes redir) {
        boolean success;
        String notification;
        try {
            userService.addUser(username, password);
            userService.addRoleToUser(username, "USER");
            notification = "User '"+username+"' has been added!";
            success = true;
        } catch (Exception e) {
            notification = "User '"+username+"' has not been added!";
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
