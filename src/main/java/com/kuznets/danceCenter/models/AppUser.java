package com.kuznets.danceCenter.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Data @NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String username;

    @Column
    @NotNull
    private String password;

    @OneToMany(mappedBy= "appUser")
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<AppUser> following = new ArrayList<>();
    @ManyToMany(mappedBy = "following")
    private List<AppUser> followers = new ArrayList<>();

    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addFollowing(AppUser user) {
        if(!following.contains(user) && !Objects.equals(user.username, this.username))
            following.add(user);
    }

    public void removeFollowing(AppUser user) {
        following.remove(user);
    }

}
