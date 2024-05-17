package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.vocal.VocalRequest;
import capstone.tunemaker.dto.vocal.VocalResponse;
import capstone.tunemaker.service.VocalTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/vocal")
@Slf4j
public class VocalTestApiController {

    private final VocalTestService vocalTestService;

    @PostMapping("/start")
    public VocalResponse handleFileUpload(@RequestBody @Validated VocalRequest request) {
        return vocalTestService.testAccuracy(request);
    }

    @GetMapping("/end")
    public void saveVoice(@AuthenticationPrincipal CustomMemberDetails memberDetails){
        vocalTestService.saveBestResult(memberDetails.getMemberId());
    }


}