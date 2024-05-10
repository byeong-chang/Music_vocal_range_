package capstone.tunemaker.service;

import capstone.tunemaker.dto.YoutubeRequest;
import capstone.tunemaker.dto.YoutubeResponse;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class YoutubeService {

    private final WebClient webClient = WebClient.create("13.125.64.249:8000");
    private final MusicRepository musicRepository;

    public Mono<YoutubeResponse> canUserSingThisSong(YoutubeRequest youtubeRequest) {

        String extractedUrlId = extractUrlId(youtubeRequest.getYoutubeUrl());
        Music music = musicRepository.findByUrlId(extractedUrlId);

        if (music != null) {
            // Music 테이블에 있는 title, url, high_pitch를 Mono<YoutubeResponse> 형식으로 반환 : 비동기 처리(백 그라운드 처리를 위해서)
            return Mono.just(new YoutubeResponse(music.getTitle(), music.getUrl(), music.getHighPitch(), null, null, null));
        }else {
            return webClient.post()
                    .uri("http://13.125.64.249:8000/youtube_extract") // 파이썬 서버의 엔드포인트
                    .body(BodyInserters.fromValue(youtubeRequest)) // 클라이언트로부터 받은 데이터
                    .retrieve()
                    .bodyToMono(YoutubeResponse.class) // 파이썬 서버의 응답을 YoutubeResponse로 변환
                    .doOnNext(response -> {
                        // 파이썬 서버로부터 받은 응답을 DB에 저장
                        Music newMusic = new Music();
                        newMusic.setTitle(response.getTitle());
                        newMusic.setUrl(response.getYoutubeUrl());
                        newMusic.setHighPitch(response.getHighPitch());
                        newMusic.setDuration(response.getDuration());
                        newMusic.setPlaylistTitle(response.getPlaylistTitle());
                        newMusic.setUploader(response.getUploader());

                        musicRepository.save(newMusic);
                    });
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
