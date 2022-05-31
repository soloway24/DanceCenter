package com.kuznets.danceCenter.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity @Data @NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column()
    @NotNull
    private String description;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "Post_Song",
            joinColumns = @JoinColumn(name = "post_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "song_id", nullable = false))
    @NotNull
    private List<Song> songs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id")
    private AppUser appUser;

    public Post(String description) {
        this.description = description;
    }


    public Post(String description, List<Song> songs) {
        this.description = description;
        this.songs = songs;
    }

    public Post(String description, List<Song> songs, AppUser user) {
        this.description = description;
        this.songs = songs;
        this.appUser = user;
    }

//    @Override
//    public String toString() {
//        return "Teacher{" +
//                "id=" + id +
//                ", name='" + name + '}';
//    }


    @Override
    public String toString() {
        return description;
    }

}
