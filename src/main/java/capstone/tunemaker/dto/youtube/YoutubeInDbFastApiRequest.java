package capstone.tunemaker.dto.youtube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeInDbFastApiRequest {
    private Double userPitch;
    private Double musicPitch;
}
