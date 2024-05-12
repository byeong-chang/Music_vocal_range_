package capstone.tunemaker.service;

import capstone.tunemaker.dto.YoutubeRequest;
import capstone.tunemaker.dto.YoutubeResponse;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.enums.Genre;
import capstone.tunemaker.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class YoutubeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MusicRepository musicRepository;

    @Transactional
    public YoutubeResponse canUserSingThisSong(YoutubeRequest youtubeRequest) {

        String extractedUrlId = extractUrlId(youtubeRequest.getYoutubeUrl());
        Music music = musicRepository.findByUrlId(extractedUrlId);

        if (music != null) {
            YoutubeResponse youtubeResponse = new YoutubeResponse();
            youtubeResponse.setYoutubeUrlId(music.getUrlId());
            return youtubeResponse;
        } else {
            HttpEntity<YoutubeRequest> requestEntity = new HttpEntity<>(youtubeRequest);
            ResponseEntity<YoutubeResponse> responseEntity = restTemplate.exchange(
                    "http://43.203.56.11:8000/youtube_extract",
                    HttpMethod.POST,
                    requestEntity,
                    YoutubeResponse.class
            );

            YoutubeResponse youtubeResponse = responseEntity.getBody();

            if (youtubeResponse != null) {
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

            return youtubeResponse;
        }
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
