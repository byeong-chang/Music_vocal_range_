package capstone.tunemaker.service;

import capstone.tunemaker.dto.VocalRequest;
import capstone.tunemaker.dto.VocalResponse;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.embeded.Pitch;
import capstone.tunemaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class VocalTestService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // 테스트 결과를 임시로 저장하는 저장소 : 종료 버튼을 누르는 순간 여기서 가장 높은 값을 데이터베이스에 저장함.
    private Map<String, Double> accuracyMap = new HashMap<>();

    public VocalResponse testAccuracy(VocalRequest request) {

        HttpEntity<VocalRequest> requestEntity = new HttpEntity<>(request);

        ResponseEntity<VocalResponse> responseEntity = restTemplate.exchange(
                "http://43.203.56.11:8000/vocal_test",
                HttpMethod.POST,
                requestEntity,
                VocalResponse.class
        );

        VocalResponse vocalResponse = responseEntity.getBody();

        // 테스트 결과를 저장소에 추가
        accuracyMap.put(request.getTarget(), vocalResponse.getAccuracy());

        return vocalResponse;
    }

    @Async
    public void saveBestResult(String username) {

        Map.Entry<String, Double> maxEntry = null;

        for (Map.Entry<String, Double> entry : accuracyMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) >= 0) {
                maxEntry = entry;
            }
        }


        if (maxEntry != null) {
            Member member = memberRepository.findByUsername(username);
            if (member != null) {
                Pitch pitch = member.getPitch();
                if (pitch == null) {
                    pitch = new Pitch();
                }
                try {
                    Field field = Pitch.class.getDeclaredField(maxEntry.getKey());
                    field.setAccessible(true);
                    field.set(pitch, maxEntry.getValue().floatValue());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                member.setPitch(pitch);
                memberRepository.save(member);
            }
        }

        // 저장소 초기화
        accuracyMap.clear();
    }
}