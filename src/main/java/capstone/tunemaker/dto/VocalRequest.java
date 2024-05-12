package capstone.tunemaker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VocalRequest {
    private String target;
    private String s3Link;
}
