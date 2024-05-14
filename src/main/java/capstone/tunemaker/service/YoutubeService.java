package capstone.tunemaker.service;

import capstone.tunemaker.dto.youtube.YoutubeRequest;
import capstone.tunemaker.dto.youtube.MusicResponse;
import capstone.tunemaker.entity.Music;
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

    public MusicResponse canUserSingThisSong(YoutubeRequest youtubeRequest) throws ExecutionException, InterruptedException {

        String extractedUrlId = extractUrlId(youtubeRequest.getYoutubeUrl());
        Music music = musicRepository.findByUrlId(extractedUrlId);

        if (music != null) {
            MusicResponse musicResponse = new MusicResponse();
            musicResponse.setYoutubeUrlId(music.getUrlId());
            return musicResponse;
        } else {
            HttpEntity<YoutubeRequest> requestEntity = new HttpEntity<>(youtubeRequest);
            CompletableFuture<MusicResponse> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<MusicResponse> responseEntity = restTemplate.exchange(
                        "http://43.203.56.11:8000/youtube_extract",
                        HttpMethod.POST,
                        requestEntity,
                        MusicResponse.class
                );
                return responseEntity.getBody();
            });

            MusicResponse musicResponse = future.get();

            if (musicResponse != null) {
                saveMusic(musicResponse);
            }
            return musicResponse;
        }
    }

    @Async
    public void saveMusic(MusicResponse musicResponse) {

        Music newMusic = new Music();

        newMusic.setTitle(musicResponse.getTitle());
        newMusic.setUrl(musicResponse.getYoutubeUrl());
        newMusic.setHighPitch(musicResponse.getHighPitch());
        newMusic.setDuration(musicResponse.getDuration());
        newMusic.setPlaylistTitle(musicResponse.getPlaylistTitle());
        newMusic.setUploader(musicResponse.getUploader());
        newMusic.setUrlId(musicResponse.getYoutubeUrlId());

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