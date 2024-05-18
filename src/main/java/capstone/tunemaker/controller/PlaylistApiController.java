package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.dto.playlist.*;
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

    @PostMapping("/playlist/create")
    public void createPlaylist(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                               @RequestBody @Validated PlaylistTitle playlistTitle) {
        playlistService.createPlaylist(memberDetails.getMemberId(), playlistTitle.getPlaylistTitle());
    }

    @PostMapping("/playlist/delete")
    public void deletePlaylist(@AuthenticationPrincipal CustomMemberDetails memberDetails,
                               @RequestBody @Validated DeletePlaylistRequest request) {
        playlistService.deletePlaylist(memberDetails.getMemberId(), request.getPlaylistId());
    }


    @PostMapping("/playlist/music/add")
    public void addMusicToPlaylist(@RequestBody @Validated AddMusicRequest request) {
        playlistService.addMusicToPlaylist(request.getPlaylistId(), request.getMusicId());
    }

    @PostMapping("/playlist/music/remove")
    public void removeMusicFromPlaylist(@RequestBody @Validated RemoveMusicRequest request) {
        playlistService.removeMusicFromPlaylist(request.getPlaylistId(), request.getMusicId());
    }

    @GetMapping("/getplaylists")
    public List<PlaylistResponse> getPlaylists(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return playlistService.getPlaylists(memberDetails.getMemberId());
    }

    @GetMapping("/playlist/{playlistId}")
    public List<MusicDetailsResponse> getMusicsFromPlaylist(@PathVariable Long playlistId){
        return playlistService.getMusicsFromPlaylist(playlistId);
    }


}
