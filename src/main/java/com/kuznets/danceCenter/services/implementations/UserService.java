package com.kuznets.danceCenter.services.implementations;

import com.kuznets.danceCenter.models.AppUser;
import com.kuznets.danceCenter.models.Role;
import com.kuznets.danceCenter.repositories.RoleRepository;
import com.kuznets.danceCenter.repositories.UserRepository;
import com.kuznets.danceCenter.services.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser addUser(String username, String password) throws Exception {
        if(userRepository.existsAppUserByUsername(username))
            throw new Exception("User already exists.");
        return userRepository.save(new AppUser(username, passwordEncoder.encode(password)));
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean userExistsByUsername(String username) {
        return userRepository.existsAppUserByUsername(username);
    }

    @Override
    public Role addRole(String name) {
        return roleRepository.save(new Role(name));
    }

    @Override
    public Role getRoleByRoleName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public boolean roleExistsByRoleName(String roleName) {
        return roleRepository.existsRoleByName(roleName);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AppUser user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public void saveUser(AppUser currentUser) {
        userRepository.save(currentUser);
    }

    @Override
    public List<AppUser> getByUsernameText(String searchQuery) {
        return userRepository.findByUsernameText(searchQuery);
    }
}
