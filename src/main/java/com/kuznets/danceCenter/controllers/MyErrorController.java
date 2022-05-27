package com.kuznets.danceCenter.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/errors")
public class MyErrorController implements ErrorController {


    @GetMapping("/addPost")
    public String errorAddPost(Model model){
        model.addAttribute("notification", "Cannot add post. It may have no songs in it.");
        return "errorPage";
    }

    @GetMapping("/addSong")
    public String errorAddSong(Model model){
        model.addAttribute("notification", "Cannot add song(songs). It may have no songs in it.");
        return "errorPage";
    }

    @GetMapping("/delete")
    public String errorDelete(Model model){
        model.addAttribute("notification", "Cannot delete song(songs). Song(songs) may not exist.");
        return "errorPage";
    }

    @GetMapping("/fileInfo")
    public String errorFileInfo(Model model){
        model.addAttribute("notification", "Couldn't fetch file info.");
        return "errorPage";
    }

    @GetMapping("/error")
    public String errorPage(@RequestParam(name = "notification", required = false) String notification, Model model){
        if(notification != null)
            model.addAttribute("notification", notification);
        return "errorPage";
    }

}
