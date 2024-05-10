package capstone.tunemaker.controller;

import capstone.tunemaker.dto.CreateMemberRequest;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join")
    public void joinMember(@RequestBody @Validated CreateMemberRequest request){

        if (!request.getPassword1().equals(request.getPassword2())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        memberService.join(request);
    }
}
