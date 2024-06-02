package capstone.tunemaker.repository;

import capstone.tunemaker.dto.inquiry.InquiryResponse;
import capstone.tunemaker.entity.Inquiry;
import capstone.tunemaker.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InquiryRepository {

    private final EntityManager em;

    public void save(Inquiry inquiry) {
        em.persist(inquiry);
    }

    public List<InquiryResponse> findAllInquiries(Member member) {
        return em.createQuery("select new capstone.tunemaker.dto.inquiry.InquiryResponse(i.title, i.contents, i.reply, i.addTime) from Inquiry as i  where i.member = :member", InquiryResponse.class)
                .setParameter("member", member)
                .getResultList();
    }

}
