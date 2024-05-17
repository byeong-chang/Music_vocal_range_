package capstone.tunemaker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Inquiry {

    @Id @GeneratedValue
    @Column(name="inquiry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    private String contents;

    private Boolean reply;

    @Column(name = "create_at")
    private LocalDateTime addTime;

}
