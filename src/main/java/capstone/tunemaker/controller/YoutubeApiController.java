package capstone.tunemaker.controller;

import capstone.tunemaker.dto.YoutubeRequest;
import capstone.tunemaker.dto.YoutubeResponse;
import capstone.tunemaker.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class YoutubeApiController {

    private final YoutubeService youtubeService;

    @PostMapping("/admin")
    public YoutubeResponse youtubeUrlMusicInfo(@RequestBody @Validated YoutubeRequest youtubeRequest){
        return youtubeService.canUserSingThisSong(youtubeRequest);
    }

}
