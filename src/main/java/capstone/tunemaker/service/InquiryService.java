package capstone.tunemaker.service;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.inquiry.InquiryResponse;
import capstone.tunemaker.entity.Inquiry;
import capstone.tunemaker.entity.Member;
import capstone.tunemaker.repository.InquiryRepository;
import capstone.tunemaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InquiryService {

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;

    public void addInquiry(Long memberId, String title, String contents) {


        Member findMember = memberRepository.findById(memberId);

        Inquiry inquiry = new Inquiry();
        inquiry.setMember(findMember);
        inquiry.setTitle(title);
        inquiry.setContents(contents);
        inquiry.setReply(false);
        inquiry.setAddTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        inquiryRepository.save(inquiry);
    }


    public List<InquiryResponse> getInquiryLists(Long memberId) {
        Member findMember = memberRepository.findByIdInquiry(memberId);
        return findMember.getInquiries().stream()
                .map(inquiry -> new InquiryResponse(inquiry.getTitle(), inquiry.getContents(), inquiry.getReply(), inquiry.getAddTime()))
                .collect(Collectors.toList());
    }

}
