package capstone.tunemaker.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMusicRequest {
    private Long playlistId;
    private Long musicId;
}
