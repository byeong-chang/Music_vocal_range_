package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.playlist.AddMusicRequest;
import capstone.tunemaker.dto.playlist.PlaylistResponse;
import capstone.tunemaker.dto.playlist.PlaylistTitle;
import capstone.tunemaker.entity.Playlist;
import capstone.tunemaker.service.PlaylistService;
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
public class PlaylistApiController {

    private final PlaylistService playlistService;

    @PostMapping("/music/playlist/create")
    public void createPlaylist(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                               @RequestBody @Validated PlaylistTitle playlistTitle) {
        playlistService.createPlaylist(memberDetails.getMemberId(), playlistTitle.getPlaylistTitle());
    }

    @GetMapping("/music/getplaylists")
    public List<PlaylistResponse> getPlaylists(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return playlistService.getPlaylists(memberDetails.getMemberId());
    }


    @PostMapping("/music/playlist/add")
    public void addMusicToPlaylist(@RequestBody @Validated AddMusicRequest request) {
        playlistService.addMusicToPlaylist(request.getPlaylistId(), request.getMusicId());
    }

    @PostMapping("/music/playlist/remove")
    public void removeMusicFromPlaylist(@RequestBody @Validated AddMusicRequest request) {
        playlistService.removeMusicFromPlaylist(request.getPlaylistId(), request.getMusicId());
    }

}
