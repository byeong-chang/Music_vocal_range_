package capstone.tunemaker.service;

import capstone.tunemaker.dto.create.CreateMemberRequest;
import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(CreateMemberRequest request){

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username you wrote is already exist.");
        }

        Member newMember = createNewMember(request);
        memberRepository.save(newMember);
    }

    private Member createNewMember(CreateMemberRequest request) {
        Member newMember = new Member();
        newMember.setNickname(request.getNickname());
        newMember.setUsername(request.getUsername());
        newMember.setPassword(bCryptPasswordEncoder.encode(request.getPassword1()));
        newMember.setRole("ROLE_ADMIN");
        newMember.setGender(request.getGender());
        return newMember;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 클라이언트로 부터 받은 username을 기반으로 DB에서 가져오는 부분
        Member userData = memberRepository.findByUsername(username);
        //log.warn("userData = {}", userData.getUsername());

        // 값의 검증 부분(만약 클라이언트로부터 받은 username이 DB에 존재한다면, UserDetails에 해당 userEntity를 반환)
        if (userData != null) {
            return new CustomMemberDetails(userData); // CustomUserDetails는 UserDetails를 커스텀한 객체(쉽게 말해, Dto이며, 따로 만들어줘야 함.)
        }

        return null;
    }
}