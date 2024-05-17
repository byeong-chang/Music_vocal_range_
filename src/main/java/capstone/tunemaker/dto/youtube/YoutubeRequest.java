package capstone.tunemaker.dto.youtube;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeRequest {

    @NotEmpty
    @Size(min=10, max=500)
    private String youtubeUrl;

    private Double highPitch;

}
