package com.kuznets.danceCenter.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    @NotNull
    private String name;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @NotNull
    private Set<Song> songs = new HashSet<>();


    public Artist(String name) {
        this.name = name;
    }

    public Artist() {
    }

    public Artist(String name, Set<Song> songs) {
        this.name = name;
        this.songs = songs;
    }

    @Override
    public String toString() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }
}
