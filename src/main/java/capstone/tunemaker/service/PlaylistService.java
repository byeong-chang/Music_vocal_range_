package capstone.tunemaker.service;

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


    public void createPlaylist(Long memberId, String title) {

        Member findMember = memberRepository.findById(memberId);
        Playlist playlist = new Playlist();
        playlist.setMember(findMember);
        playlist.setTitle(title);

        playlistRepository.save(playlist);
    }

    public List<PlaylistResponse> getPlaylists(Long memberId) {

        Member findMember = memberRepository.findById(memberId);
        List<Playlist> playlists = playlistRepository.findByMember(findMember);
        List<PlaylistResponse> playlistResponses = new ArrayList<>();

        for (Playlist playlist : playlists) {
            List<PlaylistAndMusicResponse> playlistAndMusicResponses = new ArrayList<>();
            for (PlaylistAndMusic pam : playlist.getPlaylistAndMusicList()) {
                PlaylistAndMusicResponse pamResponse = new PlaylistAndMusicResponse(
                        pam.getId(),
                        pam.getMusic().getId(),
                        pam.getMusic().getTitle()
                );
                playlistAndMusicResponses.add(pamResponse);
            }
            PlaylistResponse playlistResponse = new PlaylistResponse(
                    playlist.getId(),
                    playlist.getTitle(),
                    playlistAndMusicResponses
            );
            playlistResponses.add(playlistResponse);
        }
        return playlistResponses;
    }


    // 플레이리스트에 곡 추가
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


    // 플레이리스트에 곡 제거
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

}
