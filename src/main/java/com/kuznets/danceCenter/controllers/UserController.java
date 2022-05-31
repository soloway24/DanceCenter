package com.kuznets.danceCenter.controllers;


import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Role;
import com.kuznets.danceCenter.models.Song;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import com.kuznets.danceCenter.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserServiceInterface userService;

    @GetMapping("/getList")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }


    private void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        AppUser appUser = userService.getUserByUsername(user.getUsername());
        model.addAttribute("currentUser", appUser);
    }

    private AppUser getCurrentUser() {
        User user = Utils.getCurrentUser();
        return userService.getUserByUsername(user.getUsername());
    }

    @GetMapping
    public String users(Model model) {
        addUserToModel(model);
        model.addAttribute("users",userService.getAllUsers());
        return "users";
    }

    @GetMapping("/me")
    public String me() {
        User user = Utils.getCurrentUser();
        return "redirect:/users/" + user.getUsername();
    }

    @GetMapping("/{username}")
    public String profile(@PathVariable("username") String username, Model model) {
        addUserToModel(model);
        AppUser appUser = userService.getUserByUsername(username);
        model.addAttribute("users", Collections.singleton(appUser));
        return "users";
    }

    @GetMapping("/{username}/followers")
    public String userFollowers(@PathVariable("username") String username, Model model) {
        AppUser appUser = userService.getUserByUsername(username);
        model.addAttribute("users", appUser.getFollowers());
        addUserToModel(model);
        return "users";
    }

    @GetMapping("/{username}/following")
    public String userFollowing(@PathVariable("username") String username, Model model) {
        AppUser appUser = userService.getUserByUsername(username);
        model.addAttribute("users", appUser.getFollowing());
        addUserToModel(model);
        return "users";
    }

    @PostMapping("/{username}/toggleFollow")
    public RedirectView userToggleFollow(@PathVariable("username") String username, HttpServletRequest request,
                                         RedirectAttributes redir) {
        boolean success;
        String notification;
        try {
            AppUser appUser = userService.getUserByUsername(username);
            AppUser currentUser = getCurrentUser();
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
            AppUser user = userService.addUser(username, password);
            userService.addRoleToUser(username, "USER");
            notification = "Користувач '"+username+"' був успішно доданий!";
            success = true;
        } catch (Exception e) {
            notification = "Користувач '"+username+"' не був доданий!";
            success = false;
            redir.addFlashAttribute("error", e.getMessage());
        }

        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("notification", notification);
        if(success){
//            String referer = request.getHeader("Referer");
            return new RedirectView("/users",true);
        }else
            return new RedirectView("/errors/addPost",true);
    }

//    @PostMapping("/add")
//    @ResponseBody
//    public ResponseEntity<AppUser> addUser(@RequestParam String username, @RequestParam String password) {
//        try {
//            AppUser user = userService.addUser(username, password);
//            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/add").toUriString());
//            return ResponseEntity.created(uri).body(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}
