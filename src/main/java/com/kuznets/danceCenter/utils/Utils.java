package com.kuznets.danceCenter.utils;

import com.kuznets.danceCenter.models.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static String idListToString(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for(Long id : ids)
            sb.append(id.toString()+',');
        return sb.substring(0, sb.length() - 1);
    }

    public static List<Long> stringToIdList(String ids) {
        return Arrays.stream(ids.substring(1, ids.length() - 1).split(",")).map(Long::parseLong).collect(Collectors.toList());
    }

    public static void addAppNameToModel(Model model) {
        model.addAttribute("appName", Values.APP_NAME);
    }
}