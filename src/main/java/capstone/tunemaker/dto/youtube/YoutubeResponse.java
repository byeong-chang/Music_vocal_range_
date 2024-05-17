package capstone.tunemaker.dto.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeResponse {

    private Long Id;

    private String title;

    private String youtubeUrl;

    private Double highPitch;

    private String duration;

    private String playlistTitle;

    private String uploader;

    private String youtubeUrlId;

    private Integer keyDiff;

}
