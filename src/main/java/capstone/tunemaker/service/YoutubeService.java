package capstone.tunemaker.service;

import capstone.tunemaker.dto.youtube.YoutubeRequest;
import capstone.tunemaker.dto.music.MusicDetailsResponse;
import capstone.tunemaker.dto.youtube.YoutubeResponse;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.repository.MemberRepository;
import capstone.tunemaker.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class YoutubeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MusicRepository musicRepository;
    private final MemberRepository memberRepository;

    public YoutubeResponse canUserSingThisSong(Long memberId, YoutubeRequest youtubeRequest) throws ExecutionException, InterruptedException {

        String extractedUrlId = extractUrlId(youtubeRequest.getYoutubeUrl());
        Music music = musicRepository.findByUrlId(extractedUrlId);

        Member findMember = memberRepository.findById(memberId);
        Double highPitch = findMember.getHighPitch();
        youtubeRequest.setUserPitch(highPitch);

        log.warn(youtubeRequest.getYoutubeUrl());
        log.warn(String.valueOf(youtubeRequest.getUserPitch()));

        if (music != null) {
            YoutubeResponse youtubeResponse = new YoutubeResponse();
            youtubeResponse.setYoutubeUrlId(music.getUrlId());
            return youtubeResponse;
        } else {
            HttpEntity<YoutubeRequest> requestEntity = new HttpEntity<>(youtubeRequest);
            CompletableFuture<YoutubeResponse> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<YoutubeResponse> responseEntity = restTemplate.exchange(
                        "http://52.79.116.144:8000/youtube_extract",
                        HttpMethod.POST,
                        requestEntity,
                        YoutubeResponse.class
                );
                return responseEntity.getBody();
            });

            YoutubeResponse youtubeResponse = future.get();

            if (youtubeResponse != null) {
                saveMusic(youtubeResponse);
            }
            return youtubeResponse;
        }
    }

    @Async
    public void saveMusic(YoutubeResponse youtubeResponse) {

        Music newMusic = new Music();

        newMusic.setTitle(youtubeResponse.getTitle());
        newMusic.setUrl(youtubeResponse.getYoutubeUrl());
        newMusic.setHighPitch(youtubeResponse.getHighPitch());
        newMusic.setDuration(youtubeResponse.getDuration());
        newMusic.setPlaylistTitle(youtubeResponse.getPlaylistTitle());
        newMusic.setUploader(youtubeResponse.getUploader());
        newMusic.setUrlId(youtubeResponse.getYoutubeUrlId());

        musicRepository.save(newMusic);
    }

    private String extractUrlId(String url) {
        int start = url.indexOf("watch?v=");
        if (start != -1) {
            start += "watch?v=".length();
            int end = url.indexOf("&", start);
            if (end == -1) {
                return url.substring(start);
            } else {
                return url.substring(start, end);
            }
        }
        return null;
    }
}