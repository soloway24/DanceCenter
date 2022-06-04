package com.kuznets.danceCenter.controllers;

import com.kuznets.danceCenter.utils.Utils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class AuthenticationController {

    @GetMapping("/login-error")
    public String loginError(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errorMessage", errorMessage);
        Utils.addAppNameToModel(model);
        return "login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        Utils.addAppNameToModel(model);
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        Utils.addAppNameToModel(model);
        return "registration";
    }
}
