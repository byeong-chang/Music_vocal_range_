package capstone.tunemaker.controller;

import capstone.tunemaker.dto.youtube.YoutubeRequest;
import capstone.tunemaker.dto.youtube.YoutubeResponse;
import capstone.tunemaker.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class YoutubeApiController {

    private final YoutubeService youtubeService;

    @PostMapping("/admin")
    public YoutubeResponse youtubeUrlMusicInfo(@RequestBody @Validated YoutubeRequest youtubeRequest) throws ExecutionException, InterruptedException {
        return youtubeService.canUserSingThisSong(youtubeRequest);
    }


}
