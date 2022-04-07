package com.kuznets.danceCenter.models;

import com.kuznets.danceCenter.utils.Values;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column()
    @NotNull
    private String name;

    @Column()
    @NotNull
    private String location;

    @ManyToMany(fetch = FetchType.LAZY,
                cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "Song_Artist",
            joinColumns = @JoinColumn(name = "song_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "artist_id", nullable = false))
    @NotNull
    private Set<Artist> artists = new HashSet<>();

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @NotNull
    private Set<Post> posts = new HashSet<>();

    public Song() {
    }

    public Song(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Song(String name, Set<Artist> artists, String location) {
        this.name = name;
        this.artists = artists;
        this.location = location;
    }

    public void addArtist(Artist artist)
    {
        //check
        artists.add(artist);
        artist.getSongs().add(this);
    }


    public void removeArtist(Artist artist)
    {
        //check
        artists.remove(artist);
        artist.getSongs().remove(this);
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

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
