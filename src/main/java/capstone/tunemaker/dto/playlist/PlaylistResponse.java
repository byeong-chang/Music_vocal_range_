package capstone.tunemaker.dto.playlist;

import capstone.tunemaker.entity.PlaylistAndMusic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistResponse {
    private String playlistTitle;
    private Long playlistId;
}
