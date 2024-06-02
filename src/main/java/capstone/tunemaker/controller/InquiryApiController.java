package capstone.tunemaker.controller;

import capstone.tunemaker.dto.create.CustomMemberDetails;
import capstone.tunemaker.dto.inquiry.InquiryRequest;
import capstone.tunemaker.dto.inquiry.InquiryResponse;
import capstone.tunemaker.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class InquiryApiController {

    private final InquiryService inquiryService;

    @PostMapping("/inquiry/add")
    public String addInquiry(@AuthenticationPrincipal CustomMemberDetails memberDetails, @RequestBody @Validated InquiryRequest request) {
        inquiryService.addInquiry(memberDetails.getMemberId(), request.getTitle(), request.getContents());
        return "{\"message\":\"inquiry add ok\"}";
    }

    @GetMapping("/inquiry/list")
    public List<InquiryResponse> getInquiryLists(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return inquiryService.getInquiryLists(memberDetails.getMemberId());
    }

}
