package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.update.MemberInfoResponse;
import capstone.tunemaker.dto.update.UpdateMemberRequest;
import capstone.tunemaker.dto.update.UpdatePasswordRequest;
import capstone.tunemaker.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class MemberSettingApiController {

    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public MemberInfoResponse viewProfile(@AuthenticationPrincipal CustomMemberDetails memberDetails){
        return myPageService.retrieve(memberDetails.getMemberId());
    }

    @PostMapping("/mypage/updatebasic")
    public void updateBasic(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                                          @RequestBody @Validated UpdateMemberRequest request){
        myPageService.updateBasicInfo(memberDetails.getMemberId(), request);
    }

    @PostMapping("mypage/updatepassword")
    public void updatePassword(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                               @RequestBody @Validated UpdatePasswordRequest request){
        myPageService.updatePasswordInfo(memberDetails.getMemberId(), request);
    }

}
