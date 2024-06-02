package capstone.tunemaker.dto.vocal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VocalResponse {
    private String target;
    private Double accuracy;
    private Boolean pass;
}
