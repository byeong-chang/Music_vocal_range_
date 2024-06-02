package capstone.tunemaker.dto.music;

import capstone.tunemaker.entity.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreRequest {
    private Genre genre;
}
