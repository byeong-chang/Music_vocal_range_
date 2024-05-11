package capstone.tunemaker.service;

import capstone.tunemaker.dto.YoutubeRequest;
import capstone.tunemaker.dto.YoutubeResponse;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.enums.Genre;
import capstone.tunemaker.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class YoutubeService {

    private final WebClient webClient = WebClient.create("http://43.203.56.11:8000");
    private final MusicRepository musicRepository;

    public YoutubeResponse canUserSingThisSong(YoutubeRequest youtubeRequest) {

        String extractedUrlId = extractUrlId(youtubeRequest.getYoutubeUrl());
        Music music = musicRepository.findByUrlId(extractedUrlId);

        if (music != null) {
            // Music 테이블에 있는 title, url, high_pitch를 Mono<YoutubeResponse> 형식으로 반환 : 비동기 처리(백 그라운드 처리를 위해서)
            YoutubeResponse youtubeResponse = new YoutubeResponse();
            youtubeResponse.setYoutubeUrlId(music.getUrlId());
            return youtubeResponse;
        } else {
            // API 통신을 통해 YoutubeResponse를 가져옵니다.
            YoutubeResponse youtubeResponse = webClient.post()
                    .uri("/youtube_extract")
                    .body(BodyInserters.fromValue(youtubeRequest))
                    .retrieve()
                    .bodyToMono(YoutubeResponse.class)
                    .block(); // block() 메서드를 사용하여 동기적으로 API 요청을 보냅니다.
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