package capstone.tunemaker.service;

import capstone.tunemaker.dto.update.MemberInfoResponse;
import capstone.tunemaker.dto.update.UpdateMemberRequest;
import capstone.tunemaker.dto.update.UpdatePasswordRequest;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // [내 정보] : 사용자 정보 추출
    public MemberInfoResponse retrieve(Long userId){

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse();

        Member findMember = memberRepository.findById(userId);

        memberInfoResponse.setId(String.valueOf(findMember.getId()));
        memberInfoResponse.setUsername(findMember.getUsername());
        // Null 발생 시 체인으로 이어져서 여기서 Null 처리 했음
        Double highPitch = findMember.getHighPitch() != null ? Double.valueOf(findMember.getHighPitch()) : 0.0;
        memberInfoResponse.setHighPitch(highPitch);
        memberInfoResponse.setGender(findMember.getGender());
        memberInfoResponse.setNickname(findMember.getNickname());

        return memberInfoResponse;
    }

    // [수정] : 사용자 정보 수정
    public void updateBasicInfo(Long memberId, UpdateMemberRequest request){

        Member findMember = memberRepository.findById(memberId);

        findMember.setNickname(request.getNickname());
        findMember.setUsername(request.getUsername());
        findMember.setGender(request.getGender());

    }

    // [비밀번호 변경] : 사용자 비밀번호 수정
    public void updatePasswordInfo(Long memberId, UpdatePasswordRequest request){
        Member findMember = memberRepository.findById(memberId);

        if (!bCryptPasswordEncoder.matches(request.getOriginPassword(), findMember.getPassword())) {
            throw new IllegalArgumentException("The old password is incorrect");
        }
        if (!request.getNewPassword1().equals(request.getNewPassword2())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        findMember.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword1()));
    }

}
