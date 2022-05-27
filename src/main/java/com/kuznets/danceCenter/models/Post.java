package com.kuznets.danceCenter.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
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


    public Post(String description) {
        this.description = description;
    }

    public Post(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Post() {
    }

    public Post(String description, List<Song> songs) {
        this.description = description;
        this.songs = songs;
    }

    public void addSong(Song song)
    {
        //check
        songs.add(song);
        song.getPosts().add(this);
    }

    public void removeartist(Song song)
    {
        //check
        songs.remove(song);
        song.getPosts().remove(this);
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

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        this.description = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
