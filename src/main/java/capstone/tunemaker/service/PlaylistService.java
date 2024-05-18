package capstone.tunemaker.service;

import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.dto.playlist.PlaylistAndMusicResponse;
import capstone.tunemaker.dto.playlist.PlaylistResponse;
import capstone.tunemaker.dto.playlist.PlaylistTitle;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.Playlist;
import capstone.tunemaker.entity.PlaylistAndMusic;
import capstone.tunemaker.repository.MemberRepository;
import capstone.tunemaker.repository.MusicRepository;
import capstone.tunemaker.repository.PlaylistAndMusicRepository;
import capstone.tunemaker.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlaylistService {

    private final MemberRepository memberRepository;
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistAndMusicRepository playlistAndMusicRepository;


    // 사용자 지정 플레이리스트를 생성하는 부분
    public void createPlaylist(Long memberId, String title) {

        Member findMember = memberRepository.findById(memberId);
        Playlist playlist = new Playlist();
        playlist.setMember(findMember);
        playlist.setTitle(title);

        playlistRepository.save(playlist);
    }


    // 사용자 지정 플레이리스트를 삭제하는 부분
    public void deletePlaylist(Long memberId, Long playlistId) {
        Member findMember = memberRepository.findById(memberId);
        Playlist findPlaylist = playlistRepository.findById(playlistId);

        if (findPlaylist == null || !findPlaylist.getMember().equals(findMember)) {
            throw new IllegalArgumentException("Invalid playlist ID or the playlist does not belong to the member");
        }

        playlistRepository.delete(findPlaylist);
    }


    // 플레이리스트에 곡을 추가하는 부분
    public void addMusicToPlaylist(Long playlistId, Long musicId) {

        Playlist playlist = playlistRepository.findById(playlistId);

        if (playlist == null) {
            throw new IllegalArgumentException("Invalid playlist ID");
        }

        Music music = musicRepository.findByMusicId(musicId);
        if (music == null) {
            throw new IllegalArgumentException("Invalid music URL ID");
        }

        PlaylistAndMusic playlistAndMusic = new PlaylistAndMusic();
        playlistAndMusic.setPlaylist(playlist);
        playlistAndMusic.setMusic(music);

        playlistAndMusicRepository.save(playlistAndMusic);
    }


    // 플레이리스트에 곡을 제거하는 부분
    public void removeMusicFromPlaylist(Long playlistId, Long musicId) {

        Playlist playlist = playlistRepository.findById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("Invalid playlist ID");
        }

        Music music = musicRepository.findByMusicId(musicId);
        if (music == null) {
            throw new IllegalArgumentException("Invalid music URL ID");
        }

        PlaylistAndMusic playlistAndMusic = playlistAndMusicRepository.findByPlaylistAndMusic(playlist, music);
        if (playlistAndMusic == null) {
            throw new IllegalArgumentException("Music not found in the playlist");
        }

        playlistAndMusicRepository.delete(playlistAndMusic);
    }

    // 사용자가 만든 플레이리스트의 목록을 반환하는 부분
    public List<PlaylistResponse> getPlaylists(Long memberId) {

        Member findMember = memberRepository.findById(memberId);
        List<Playlist> playlists = playlistRepository.findByMember(findMember);
        List<PlaylistResponse> playlistResponses = new ArrayList<>();

        for (Playlist playlist : playlists) {
            playlistResponses.add(new PlaylistResponse(playlist.getTitle(), playlist.getId()));
        }

        return playlistResponses;
    }

    // 사용자 지정 플레이리스트 내부에 있는 곡들을 반환하는 부분
    public List<MusicDetailsResponse> getMusicsFromPlaylist(Long playlistId) {

        Playlist playlist = playlistRepository.findById(playlistId);

        if (playlist == null) {
            throw new IllegalArgumentException("Invalid playlist ID");
        }

        List<MusicDetailsResponse> musicDetailsResponses = new ArrayList<>();

        for (PlaylistAndMusic pliMusic : playlist.getPlaylistAndMusicList()) {
            Music music = pliMusic.getMusic();
            MusicDetailsResponse musicResponse = new MusicDetailsResponse(
                    music.getId(),
                    music.getTitle(),
                    music.getUrl(),
                    music.getHighPitch(),
                    music.getDuration(),
                    music.getPlaylistTitle(),
                    music.getUploader(),
                    music.getUrlId()
            );
            musicDetailsResponses.add(musicResponse);
        }
        return musicDetailsResponses;
    }


}
