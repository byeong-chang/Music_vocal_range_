package capstone.tunemaker.entity;

//import capstone.tunemaker.entity.embeded.Pitch;
import capstone.tunemaker.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String username;

    private String password;

    @Column(name = "high_pitch")
    private Double highPitch;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Playlist> playlistList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Inquiry> inquiries = new ArrayList<>();
}
