package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.utils.Utils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/errors")
public class MyErrorController implements ErrorController {

    @GetMapping("/addPost")
    public String errorAddPost(Model model){
        Utils.addAppNameToModel(model);
        model.addAttribute("notification", "Cannot add post. It may have no songs in it.");
        return "errorPage";
    }

    @GetMapping("/addSong")
    public String errorAddSong(Model model){
        Utils.addAppNameToModel(model);
        model.addAttribute("notification", "Cannot add song(songs). It may have no songs in it.");
        return "errorPage";
    }

    @GetMapping("/follow")
    public String errorFollow(Model model){
        Utils.addAppNameToModel(model);
        model.addAttribute("notification", "Cannot follow the user. It may not exist.");
        return "errorPage";
    }

    @GetMapping("/deleteSong")
    public String errorDeleteSong(Model model){
        Utils.addAppNameToModel(model);
        model.addAttribute("notification", "Cannot delete song(songs). Song(songs) may not exist.");
        return "errorPage";
    }

    @GetMapping("/fileInfo")
    public String errorFileInfo(Model model){
        Utils.addAppNameToModel(model);
        model.addAttribute("notification", "Couldn't fetch file info. Max size of uploaded files is 200MB.");
        return "errorPage";
    }

    @GetMapping("/error")
    public String errorPage(Model model){
        Utils.addAppNameToModel(model);
        return "errorPage";
    }

}
