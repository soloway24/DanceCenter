package com.kuznets.danceCenter.services.interfaces;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Artist;
import com.kuznets.danceCenter.models.Role;
import com.kuznets.danceCenter.models.Song;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;

import java.util.List;

public interface UserServiceInterface {

    AppUser addUser(String username, String password) throws Exception;
    AppUser getUserByUsername(String username);
    List<AppUser> getAllUsers();

    boolean userExistsByUsername(String username);

    Role addRole(String name);
    Role getRoleByRoleName(String roleName);
    boolean roleExistsByRoleName(String roleName);
    List<Role> getAllRoles();
    void addRoleToUser(String username, String roleName);

    void saveUser(AppUser currentUser);

    AppUser getCurrentUser();
}
