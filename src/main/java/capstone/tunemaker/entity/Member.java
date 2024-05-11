package capstone.tunemaker.entity;

import capstone.tunemaker.entity.embeded.Pitch;
import capstone.tunemaker.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String nickname;

    private String username;

    private String password;

    @Column(name = "high_pitch")
    private String highPitch;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String role;

    private Pitch pitch;

}
