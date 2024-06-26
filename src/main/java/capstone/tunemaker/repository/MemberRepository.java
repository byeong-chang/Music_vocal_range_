package capstone.tunemaker.repository;

import capstone.tunemaker.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findById(Long memberId){
        return em.find(Member.class, memberId);
    }

    public Member findByIdInquiry(Long memberId){
        return em.createQuery("select m from Member m left join fetch m.inquiries where m.id = :memberId", Member.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public Boolean existsByUsername(String username) {
        Long count = em.createQuery("select count(u) from Member u where u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }

    public Member findByUsername(String username){
        return em.createQuery("select m from Member as m where m.username=:username", Member.class)
                .setParameter("username", username)
                .getSingleResult();
    }

}
