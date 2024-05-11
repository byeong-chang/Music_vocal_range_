package capstone.tunemaker.controller;

import capstone.tunemaker.dto.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class MemberSettingApiController {

    @GetMapping("/mypage")
    public CustomMemberDetails viewProfile(@AuthenticationPrincipal CustomMemberDetails memberInfo){
        return memberInfo;
    }


}
