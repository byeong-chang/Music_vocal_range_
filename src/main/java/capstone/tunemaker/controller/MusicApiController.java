package capstone.tunemaker.controller;

import capstone.tunemaker.dto.music.MusicDetailsRequest;
import capstone.tunemaker.dto.music.SearchKeyword;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class MusicApiController {

    private final MusicService musicService;


    @PostMapping("/music/details")
    public MusicDetailsResponse showMusicDetails(@RequestBody @Validated MusicDetailsRequest request) {
        return musicService.searchMusicDetails(request.getYoutubeUrlId());
    }


    @PostMapping("/music/search")
    public List<MusicDetailsResponse> searchMusic(@RequestBody @Validated SearchKeyword keyword) {
        return musicService.keywordSearch(keyword);
    }
}
