package capstone.tunemaker.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistAndMusicResponse {
    private Long id;
    private Long musicId;
    private String musicTitle;
}