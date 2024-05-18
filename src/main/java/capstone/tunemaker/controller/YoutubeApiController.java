package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.youtube.YoutubeRequest;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.dto.youtube.YoutubeResponse;
import capstone.tunemaker.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class YoutubeApiController {

    private final YoutubeService youtubeService;
    private final ConcurrentHashMap<Long, CompletableFuture<YoutubeResponse>> responseCache = new ConcurrentHashMap<>();



    @PostMapping("/admin/youtube")
    public YoutubeResponse youtubeUrlMusicInfo(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                                               @RequestBody @Validated YoutubeRequest youtubeRequest) throws ExecutionException, InterruptedException {
        return youtubeService.canUserSingThisSong(memberDetails.getMemberId(), youtubeRequest);
    }

}
