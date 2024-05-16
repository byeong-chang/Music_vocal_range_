package capstone.tunemaker.service;

import capstone.tunemaker.dto.music.MusicDetailsRequest;
import capstone.tunemaker.dto.music.SearchKeyword;
import capstone.tunemaker.dto.update.MemberInfoResponse;
import capstone.tunemaker.dto.youtube.MusicResponse;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.Playlist;
import capstone.tunemaker.entity.PlaylistAndMusic;
import capstone.tunemaker.entity.enums.Genre;
import capstone.tunemaker.repository.MemberRepository;
import capstone.tunemaker.repository.MusicRepository;
import capstone.tunemaker.repository.PlaylistAndMusicRepository;
import capstone.tunemaker.repository.PlaylistRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MusicService {

    private final MemberRepository memberRepository;
    private final MusicRepository musicRepository;


    // 노래 "하나" 터치했을 때, 노래의 자세한 정보
    public MusicResponse searchMusicDetails(MusicDetailsRequest request) {

        Music findMusic = musicRepository.findByUrlId(request.getYoutubeUrlId());

        MusicResponse musicResponse = new MusicResponse();

        musicResponse.setTitle(findMusic.getTitle());
        musicResponse.setUploader(findMusic.getUploader());
        musicResponse.setHighPitch(findMusic.getHighPitch());
        musicResponse.setDuration(findMusic.getDuration());
        musicResponse.setPlaylistTitle(findMusic.getPlaylistTitle());
        musicResponse.setYoutubeUrlId(findMusic.getUrlId());
        musicResponse.setYoutubeUrl(findMusic.getUrl());
        musicResponse.setId(findMusic.getId());

        return musicResponse;
    }


    // 키워드가 포함되어 있는 노래들을 리스트 형식으로 반환
    public List<MusicResponse> keywordSearch(SearchKeyword keyword) {
        List<Music> musics = musicRepository.findByTitleContaining(keyword.getKeyword());
        return musics.stream().map(music -> {
            MusicResponse musicResponse = new MusicResponse();
            musicResponse.setTitle(music.getTitle());
            musicResponse.setUploader(music.getUploader());
            musicResponse.setHighPitch(music.getHighPitch());
            musicResponse.setDuration(music.getDuration());
            musicResponse.setPlaylistTitle(music.getPlaylistTitle());
            musicResponse.setYoutubeUrlId(music.getUrlId());
            musicResponse.setYoutubeUrl(music.getUrl());
            musicResponse.setId(music.getId());
            return musicResponse;
        }).collect(Collectors.toList());
    }


    // 홈 버튼을 눌렀을 때 추천되는 노래 10곡씩 추출
    public Map<String, List<MusicResponse>> get10MusicByGenreAndPitch(Long memberId) {

        Map<String, List<MusicResponse>> result = new ConcurrentHashMap<>();

        Member findMember = memberRepository.findById(memberId);
        Double highPitch = findMember.getHighPitch();

        if (highPitch != null) {
            // 사용자 음계보다 낮은 음악 반환
            List<Music> musics = musicRepository.find10ByPitch(highPitch);
            List<MusicResponse> musicResponses = convertToMusicResponse(musics);
            result.put("Pitch", musicResponses);
        }

        // 장르별 음악 반환
        for (Genre genre : Genre.values()) {
            List<Music> musics = musicRepository.find10ByGenre(genre);
            List<MusicResponse> musicResponses = convertToMusicResponse(musics);
            result.put(genre.name(), musicResponses);
        }

        return result;
    }

    private List<MusicResponse> convertToMusicResponse(List<Music> musics) {
        return musics.stream().map(music -> {
            MusicResponse musicResponse = new MusicResponse();
            musicResponse.setId(music.getId());
            musicResponse.setTitle(music.getTitle());
            musicResponse.setYoutubeUrl(music.getUrl());
            musicResponse.setHighPitch(music.getHighPitch());
            musicResponse.setDuration(music.getDuration());
            musicResponse.setPlaylistTitle(music.getPlaylistTitle());
            musicResponse.setUploader(music.getUploader());
            musicResponse.setYoutubeUrlId(music.getUrlId());
            return musicResponse;
        }).collect(Collectors.toList());
    }
}
