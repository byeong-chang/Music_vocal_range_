package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.music.GenreRequest;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class HomeApiController {

    private final MusicService musicService;

    @GetMapping("/home")
    public List<MusicDetailsResponse> loadHome(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return musicService.get10MusicByPitch(memberDetails.getMemberId());
    }

    @PostMapping("/home/genre")
    public List<MusicDetailsResponse> recommendByGenre(@RequestBody @Validated GenreRequest request) {
        return musicService.get10MusicByGenre(request.getGenre());
    }

}
