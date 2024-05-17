package capstone.tunemaker.dto.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicDetailsResponse {

    private Long Id;

    private String title;

    private String youtubeUrl;

    private Double highPitch;

    private String duration;

    private String playlistTitle;

    private String uploader;

    private String youtubeUrlId;

}