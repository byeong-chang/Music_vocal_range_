package capstone.tunemaker.service;

import capstone.tunemaker.dto.youtube.YoutubeFastApiRequest;
import capstone.tunemaker.dto.youtube.YoutubeInDbFastApiRequest;
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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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



        if (music != null) { // 데이터베이스에 있는 경우

            YoutubeInDbFastApiRequest youtubeInDbFastApiRequest = new YoutubeInDbFastApiRequest();

            youtubeInDbFastApiRequest.setUserPitch(highPitch);
            youtubeInDbFastApiRequest.setMusicPitch(music.getHighPitch());


            HttpEntity<YoutubeInDbFastApiRequest> requestEntity = new HttpEntity<>(youtubeInDbFastApiRequest);
            CompletableFuture<YoutubeResponse> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<YoutubeResponse> responseEntity = restTemplate.exchange(
                        "http://3.34.75.131:8000/youtube_extract_check",
                        HttpMethod.POST,
                        requestEntity,
                        YoutubeResponse.class
                );

                return responseEntity.getBody();
            });


            YoutubeResponse youtubeResponse = future.get(); // fastAPI로부터 받은 YoutubeResponse를 사용

            youtubeResponse.setId(music.getId());
            youtubeResponse.setYoutubeUrl(music.getUrl());
            youtubeResponse.setYoutubeUrlId(music.getUrlId());
            youtubeResponse.setTitle(music.getTitle());
            youtubeResponse.setHighPitch(music.getHighPitch());
            youtubeResponse.setDuration(music.getDuration());
            youtubeResponse.setKeyDiff(youtubeResponse.getKeyDiff());

            return youtubeResponse;
        } else { // 데이터베이스에 없는 경우

            YoutubeFastApiRequest youtubeFastApiRequest = new YoutubeFastApiRequest();

            youtubeFastApiRequest.setYoutubeUrl(youtubeRequest.getYoutubeUrl());
            youtubeFastApiRequest.setUserPitch(highPitch);

            HttpEntity<YoutubeFastApiRequest> requestEntity = new HttpEntity<>(youtubeFastApiRequest);
            CompletableFuture<YoutubeResponse> future = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<YoutubeResponse> responseEntity = restTemplate.exchange(
                        "http://3.34.75.131:8000/youtube_extract",
                        HttpMethod.POST,
                        requestEntity,
                        YoutubeResponse.class
                );
                return responseEntity.getBody();
            });



            YoutubeResponse youtubeResponse = future.get();


            youtubeResponse.setYoutubeUrlId(extractedUrlId);

            if (!youtubeResponse.getIsYoutubeUrl()) {
                throw new RuntimeException("정상적인 유튜브 URL이 아닙니다. 다시 입력해주세요.");
            }

            saveMusic(youtubeResponse);
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

        String urlId = null;

        if (url.contains("watch?v=")) {
            int start = url.indexOf("watch?v=") + "watch?v=".length();
            int end = url.indexOf("&", start);
            if (end == -1) {
                urlId = url.substring(start);
            } else {
                urlId = url.substring(start, end);
            }
        } else if (url.contains("youtu.be/")) {
            int start = url.indexOf("youtu.be/") + "youtu.be/".length();
            int end = url.indexOf("?", start);
            if (end == -1) {
                urlId = url.substring(start);
            } else {
                urlId = url.substring(start, end);
            }
        }

        return urlId;
    }
}