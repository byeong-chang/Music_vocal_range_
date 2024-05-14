package capstone.tunemaker.dto.update;

import capstone.tunemaker.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoResponse {
    private String id;
    private String nickname;
    private String username;
    private Gender gender;
    private Double highPitch;
}

