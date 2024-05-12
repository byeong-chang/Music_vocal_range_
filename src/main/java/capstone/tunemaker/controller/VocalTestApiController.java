package capstone.tunemaker.controller;

import capstone.tunemaker.dto.CustomMemberDetails;
import capstone.tunemaker.dto.VocalRequest;
import capstone.tunemaker.dto.VocalResponse;
import capstone.tunemaker.service.VocalTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/test")
public class VocalTestApiController {

    private final VocalTestService vocalTestService;

    @PostMapping("/admin/start")
    public VocalResponse handleFileUpload(@RequestBody @Validated VocalRequest request) {
        return vocalTestService.testAccuracy(request);
    }

    @GetMapping("/admin/end")
    public void saveVoice(@AuthenticationPrincipal CustomMemberDetails memberDetails){
        vocalTestService.saveBestResult(memberDetails.getUsername());
    }
}