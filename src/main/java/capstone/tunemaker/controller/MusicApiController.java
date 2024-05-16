package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.music.MusicDetailsRequest;
import capstone.tunemaker.dto.music.SearchKeyword;
import capstone.tunemaker.dto.playlist.PlaylistTitle;
import capstone.tunemaker.dto.youtube.MusicResponse;
import capstone.tunemaker.entity.enums.Genre;
import capstone.tunemaker.service.MusicService;
import capstone.tunemaker.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class MusicApiController {

    private final MusicService musicService;


    @GetMapping("/music/details")
    public MusicResponse showMusicDetails(@RequestBody @Validated MusicDetailsRequest request) {
        return musicService.searchMusicDetails(request);
    }


    @PostMapping("/music/search")
    public List<MusicResponse> searchMusic(@RequestBody @Validated SearchKeyword keyword) {
        return musicService.keywordSearch(keyword);
    }
}
