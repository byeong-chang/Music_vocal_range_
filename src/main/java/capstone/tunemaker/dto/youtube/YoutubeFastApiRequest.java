package capstone.tunemaker.dto.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeFastApiRequest {
    private String youtubeUrl;
    private Double userPitch;
}
