package capstone.tunemaker.service;

import capstone.tunemaker.dto.music.MusicDetailsRequest;
import capstone.tunemaker.dto.music.SearchKeyword;
import capstone.tunemaker.dto.update.MemberInfoResponse;
import capstone.tunemaker.dto.youtube.MusicResponse;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MusicService {

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
}
