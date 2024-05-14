package capstone.tunemaker.dto.update;

import capstone.tunemaker.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateMemberRequest {

    @NotEmpty
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "닉네임은 특수문자를 포함하지 않은 2~10자리여야 합니다.")
    private String nickname;

    @NotEmpty
    @Size(min=8)
    @Email
    private String username;

    @NotNull
    private Gender gender;
}
