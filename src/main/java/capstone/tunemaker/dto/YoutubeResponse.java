package capstone.tunemaker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YoutubeResponse {

    private String title;

    private String youtubeUrl;

    private Double highPitch;

    private String duration;

    private String playlistTitle;

    private String uploader;

}