package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CreateMemberRequest;
import capstone.tunemaker.service.MemberService;
import capstone.tunemaker.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private  final TokenBlacklistService tokenBlacklistService;
    private final MemberService memberService;

    @PostMapping("/join")
    public void joinMember(@RequestBody @Validated CreateMemberRequest request){
        if (!request.isPasswordMatch()) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        memberService.join(request);
    }

    @GetMapping("/admin/logout")
    public void logout(HttpServletRequest request){
        String token = tokenBlacklistService.extractToken(request.getHeader("Authorization"));
        if (token != null) {
            tokenBlacklistService.blacklist(token);
        }
    }

}
