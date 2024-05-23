package capstone.tunemaker.service;

import capstone.tunemaker.dto.vocal.PitchConverter;
import capstone.tunemaker.dto.vocal.VocalRequest;
import capstone.tunemaker.dto.vocal.VocalResponse;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.embeded.Pitch;
import capstone.tunemaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

        log.error(request.getTarget());
        log.error(request.getS3Link());

        HttpEntity<VocalRequest> requestEntity = new HttpEntity<>(request);

        ResponseEntity<VocalResponse> responseEntity = restTemplate.exchange(
                "http://13.124.174.190:8000/vocal_test",
                HttpMethod.POST,
                requestEntity,
                VocalResponse.class
        );

        VocalResponse vocalResponse = responseEntity.getBody();

        // 모듈로부터 얻은 데이터 저장소에 추가
        String target = vocalResponse.getTarget();
        Double accuracy = vocalResponse.getAccuracy();


        if (!accuracyMap.containsKey(target) || accuracyMap.get(target) < accuracy) {
            accuracyMap.put(target, accuracy);
        }

        return vocalResponse;
    }

    public void saveBestResult(Long memberId) {

        Member findMember = memberRepository.findById(memberId);

        Pitch pitch = new Pitch();

        for (Field field : pitch.getClass().getDeclaredFields()) {

            String target = field.getName();
            Double accuracy = accuracyMap.get(target);

            if (accuracy != null) {
                try {
                    field.setAccessible(true);
                    field.set(pitch, accuracy.floatValue());

                    if (accuracy >= 30.0) {
                        findMember.setHighPitch(PitchConverter.convertPitchToHz(target));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 변경된 Pitch 객체를 데이터베이스에 저장
        memberRepository.save(findMember);
    }

}