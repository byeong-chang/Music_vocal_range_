package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.youtube.YoutubeRequest;
import capstone.tunemaker.dto.youtube.MusicResponse;
import capstone.tunemaker.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class YoutubeApiController {

    private final YoutubeService youtubeService;

    @PostMapping("/admin/youtube")
    public MusicResponse youtubeUrlMusicInfo(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                                             @RequestBody @Validated YoutubeRequest youtubeRequest) throws ExecutionException, InterruptedException {
        return youtubeService.canUserSingThisSong(memberDetails.getMemberId(), youtubeRequest);
    }


}
