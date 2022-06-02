package com.kuznets.danceCenter.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column()
    @NotNull
    private String title;

    @Column()
    @NotNull
    private String location;

    @ManyToMany(fetch = FetchType.EAGER,
                cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "Song_Artist",
            joinColumns = @JoinColumn(name = "song_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "artist_id", nullable = false))
    @NotNull
    private List<Artist> artists = new ArrayList<>();

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @NotNull
    private List<Post> posts = new ArrayList<>();

    public Song(String title, String location) {
        this.title = title;
        this.location = location;
    }

    public Song(String title, List<Artist> artists, String location) {
        this.title = title;
        this.artists = artists;
        this.location = location;
    }

    @Override
    public String toString() {
        return title;
    }


}
