package capstone.tunemaker.dto.vocal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VocalResponse {
    private String target;
    private Double accuracy;
    private Boolean pass;
}
