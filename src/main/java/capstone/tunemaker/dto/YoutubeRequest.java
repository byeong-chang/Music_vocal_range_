package capstone.tunemaker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YoutubeRequest {

    @NotEmpty
    @Size(min=10, max=500)
    private String youtubeUrl;

}
