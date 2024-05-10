package capstone.tunemaker.entity;


import capstone.tunemaker.entity.enums.Genre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class Music {

    @Id
    @GeneratedValue
    @Column(name = "music_id")
    private Long id;

    private String title;

    private String url;

    private String urlId;

    private String playlistTitle;

    private String duration;

    private String uploader;

    private Double highPitch;

    @OneToMany(mappedBy = "music")
    private List<Playlist> playlist;

    @Enumerated(EnumType.STRING)
    private Genre genre;

}

