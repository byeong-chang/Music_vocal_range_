package capstone.tunemaker.controller;

import capstone.tunemaker.dto.music.MusicDetailsRequest;
import capstone.tunemaker.dto.music.SearchKeyword;
import capstone.tunemaker.dto.youtube.MusicResponse;
import capstone.tunemaker.entity.enums.Genre;
import capstone.tunemaker.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/music")
@Slf4j
public class MusicApiController {

    private final MusicService musicService;

    @GetMapping("/details")
    public MusicResponse showMusicDetails(@RequestBody @Validated MusicDetailsRequest request) {
        return musicService.searchMusicDetails(request);
    }


    @PostMapping("/search")
    public List<MusicResponse> searchMusic(@RequestBody @Validated SearchKeyword keyword) {
        return musicService.keywordSearch(keyword);
    }

    @GetMapping("/admin/home")
    @PostMapping("/search")
    public Map<Genre, List<MusicResponse>> loadHome() {
        return musicService.getTop10MusicByGenre();
    }


}
