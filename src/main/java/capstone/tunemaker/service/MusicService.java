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
    public MusicDetailsResponse searchMusicDetails(MusicDetailsRequest request) {

        Music findMusic = musicRepository.findByUrlId(request.getYoutubeUrlId());

        MusicDetailsResponse musicDetailsResponse = new MusicDetailsResponse();

        musicDetailsResponse.setTitle(findMusic.getTitle());
        musicDetailsResponse.setUploader(findMusic.getUploader());
        musicDetailsResponse.setHighPitch(findMusic.getHighPitch());
        musicDetailsResponse.setDuration(findMusic.getDuration());
        musicDetailsResponse.setPlaylistTitle(findMusic.getPlaylistTitle());
        musicDetailsResponse.setYoutubeUrlId(findMusic.getUrlId());
        musicDetailsResponse.setYoutubeUrl(findMusic.getUrl());
        musicDetailsResponse.setId(findMusic.getId());

        return musicDetailsResponse;
    }


    // 키워드가 포함되어 있는 노래들을 리스트 형식으로 반환
    public List<MusicDetailsResponse> keywordSearch(SearchKeyword keyword) {
        List<Music> musics = musicRepository.findByTitleContaining(keyword.getKeyword());
        return musics.stream().map(music -> {
            MusicDetailsResponse musicDetailsResponse = new MusicDetailsResponse();
            musicDetailsResponse.setTitle(music.getTitle());
            musicDetailsResponse.setUploader(music.getUploader());
            musicDetailsResponse.setHighPitch(music.getHighPitch());
            musicDetailsResponse.setDuration(music.getDuration());
            musicDetailsResponse.setPlaylistTitle(music.getPlaylistTitle());
            musicDetailsResponse.setYoutubeUrlId(music.getUrlId());
            musicDetailsResponse.setYoutubeUrl(music.getUrl());
            musicDetailsResponse.setId(music.getId());
            return musicDetailsResponse;
        }).collect(Collectors.toList());
    }


    // 홈 버튼을 눌렀을 때 사용자 음계보다 낮은 노래 10곡 추천
    public List<MusicDetailsResponse> get10MusicByPitch(Long memberId) {

        List<MusicDetailsResponse> musicDetailsResponses = new ArrayList<>();

        Member findMember = memberRepository.findById(memberId);
        Double highPitch = findMember.getHighPitch();

        List<Music> extracted10ByPitch = musicRepository.find10ByPitch(highPitch);
        return convertToMusicResponse(extracted10ByPitch);
    }

    // 입력된 장르에 맞는 노래 10곡 추천
    public List<MusicDetailsResponse> get10MusicByGenre(Genre genre) {

        List<MusicDetailsResponse> musicDetailsResponses = new ArrayList<>();

        // 장르별 음악 반환{
        List<Music> extracted10ByGenre = musicRepository.find10ByGenre(genre);
        return convertToMusicResponse(extracted10ByGenre);
    }


    private List<MusicDetailsResponse> convertToMusicResponse(List<Music> musics) {
        return musics.stream().map(music -> {
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
        }).collect(Collectors.toList());
    }
}
