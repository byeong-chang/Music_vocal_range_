package capstone.tunemaker.service;

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

        log.error(request.getTarget());
        log.error(request.getS3Link());

        HttpEntity<VocalRequest> requestEntity = new HttpEntity<>(request);

        ResponseEntity<VocalResponse> responseEntity = restTemplate.exchange(
                "http://15.164.85.121:8000/vocal_test",
                HttpMethod.POST,
                requestEntity,
                VocalResponse.class
        );

        VocalResponse vocalResponse = responseEntity.getBody();

        // 모듈로부터 얻은 데이터 저장소에 추가
        String target = vocalResponse.getTarget();
        Double accuracy = vocalResponse.getAccuracy();

        log.error("fastAPI return target = {}", target);
        log.error("fastAPI return target = {}", accuracy);

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

            log.info("target = {}", target);
            log.info("accuracy = {}", accuracy);

            if (accuracy != null) {
                try {
                    field.setAccessible(true);
                    field.set(pitch, accuracy.floatValue());

                    if (accuracy >= 30.0) {
                        findMember.setHighPitch(convertPitchToHz(target));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 변경된 Pitch 객체를 데이터베이스에 저장
        memberRepository.save(findMember);
    }

    public Double convertPitchToHz(String target) {
        Map<String, Double> PITCH_TO_HZ = new HashMap<String, Double>() {{
            put("C3", 130.81);
            put("CSharp3", 138.59);
            put("D3", 146.83);
            put("DSharp3", 155.56);
            put("E3", 164.81);
            put("F3", 174.61);
            put("FSharp3", 185.00);
            put("G3", 196.00);
            put("GSharp3", 207.65);
            put("A3", 220.00);
            put("ASharp3", 233.08);
            put("B3", 246.94);
            put("C4", 261.63);
            put("CSharp4", 277.18);
            put("D4", 293.66);
            put("DSharp4", 311.13);
            put("E4", 329.63);
            put("F4", 349.23);
            put("FSharp4", 369.99);
            put("G4", 392.00);
            put("GSharp4", 415.30);
            put("A4", 440.00);
            put("ASharp4", 466.16);
            put("B4", 493.88);
            put("C5", 523.25);
            put("CSharp5", 554.37);
            put("D5", 587.33);
            put("DSharp5", 622.25);
            put("E5", 659.25);
            put("F5", 698.46);
            put("FSharp5", 739.99);
            put("G5", 783.99);
            put("GSharp5", 830.61);
            put("A5", 880.00);
        }};
        return PITCH_TO_HZ.get(target);
    }


}