package com.kuznets.danceCenter.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @Data @NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 2048)
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

    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date date;

    public Post(String description) {
        this.description = description;
    }


    public Post(String description, List<Song> songs) {
        this.description = description;
        this.songs = songs;
        Instant instant = new Date().toInstant().truncatedTo(ChronoUnit.MINUTES);
        date = Date.from(instant);
    }

    public Post(String description, List<Song> songs, AppUser user) {
        this.description = description;
        this.songs = songs;
        this.appUser = user;
        Instant instant = new Date().toInstant().truncatedTo(ChronoUnit.MINUTES);
        date = Date.from(instant);
    }

    @Override
    public String toString() {
        return description;
    }

}
