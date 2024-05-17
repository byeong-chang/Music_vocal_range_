package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class HomeApiController {

    private final MusicService musicService;

    @GetMapping("/home")
    public Map<String, List<MusicDetailsResponse>> loadHome(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return musicService.get10MusicByGenreAndPitch(memberDetails.getMemberId());
    }

}
