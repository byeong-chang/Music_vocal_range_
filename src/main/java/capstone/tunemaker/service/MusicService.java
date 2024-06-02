package capstone.tunemaker.service;

import capstone.tunemaker.dto.music.MusicDetailsRequest;
import capstone.tunemaker.dto.music.SearchKeyword;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.enums.Genre;
import capstone.tunemaker.repository.MemberRepository;
import capstone.tunemaker.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
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
    public MusicDetailsResponse searchMusicDetails(String youtubeUrlId) {
        Music findMusic = musicRepository.findByUrlId(youtubeUrlId);
        return convertToMusicResponse(findMusic);
    }

    // 키워드가 포함되어 있는 노래들을 리스트 형식으로 반환
    public List<MusicDetailsResponse> keywordSearch(SearchKeyword keyword) {

        if (keyword.getKeyword() == null || keyword.getKeyword().trim().isEmpty()) {
            throw new IllegalStateException("키워드를 입력 해주세요.");
        }
        List<Music> musics = musicRepository.findByTitleContaining(keyword.getKeyword());
        return musics.stream().map(this::convertToMusicResponse).collect(Collectors.toList());
    }

    // 홈 버튼을 눌렀을 때 사용자 음계보다 낮은 노래 10곡 추천
    public List<MusicDetailsResponse> get10MusicByPitch(Long memberId) {
        Member findMember = memberRepository.findById(memberId);
        Double highPitch = findMember.getHighPitch();
        List<Music> extracted10ByPitch = musicRepository.find10ByPitch(highPitch);
        return extracted10ByPitch.stream().map(this::convertToMusicResponse).collect(Collectors.toList());
    }

    // 입력된 장르에 맞는 노래 10곡 추천
    public List<MusicDetailsResponse> get10MusicByGenre(Genre genre) {
        List<Music> extracted10ByGenre = musicRepository.find10ByGenre(genre);
        return extracted10ByGenre.stream().map(this::convertToMusicResponse).collect(Collectors.toList());
    }

    private MusicDetailsResponse convertToMusicResponse(Music music) {
        MusicDetailsResponse musicDetailsResponse = new MusicDetailsResponse();
        musicDetailsResponse.setId(music.getId());
        musicDetailsResponse.setTitle(music.getTitle());
        musicDetailsResponse.setYoutubeUrl(music.getUrl());
        musicDetailsResponse.setHighPitch(music.getHighPitch());
        musicDetailsResponse.setDuration(music.getDuration());
        musicDetailsResponse.setPlaylistTitle(music.getPlaylistTitle());
        musicDetailsResponse.setUploader(music.getUploader());
        musicDetailsResponse.setYoutubeUrlId(music.getUrlId());
        return musicDetailsResponse;
    }
}