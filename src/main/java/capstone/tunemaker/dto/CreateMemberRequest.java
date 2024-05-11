package capstone.tunemaker.dto;

import capstone.tunemaker.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMemberRequest {

    @NotEmpty
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "닉네임은 특수문자를 포함하지 않은 2~10자리여야 합니다.")
    private String nickname;

    @NotEmpty
    @Size(min=8)
    @Email
    private String username;

    @NotEmpty
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
    private String password1;

    @NotEmpty
    private String password2;

    @NotNull
    private Gender gender;

}
