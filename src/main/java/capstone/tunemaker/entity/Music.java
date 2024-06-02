package capstone.tunemaker.entity;


import capstone.tunemaker.entity.enums.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    private String title;

    private String url;

    private String urlId;

    private String playlistTitle;

    private String duration;

    private String uploader;

    private Double highPitch;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @OneToMany(mappedBy = "music", cascade = CascadeType.REMOVE)
    private List<PlaylistAndMusic> playlist = new ArrayList<>();

}

