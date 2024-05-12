package capstone.tunemaker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VocalResponse {
    private String target;
    private Double accuracy;
}
