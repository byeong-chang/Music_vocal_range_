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
    public String joinMember(@RequestBody @Validated CreateMemberRequest request){

        if (!request.getPassword1().equals(request.getPassword2())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        memberService.join(request);

        return "{\"message\":\"ok\"}";
    }

    @GetMapping("/admin/logout")
    public void logout(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.split(" ")[1];
            tokenBlacklistService.blacklist(token);
        }
    }

}
