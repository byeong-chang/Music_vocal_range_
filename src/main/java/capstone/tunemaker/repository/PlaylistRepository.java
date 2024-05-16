package capstone.tunemaker.repository;

import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.Playlist;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaylistRepository {

    private final EntityManager em;

    public void save(Playlist playlist) {
        em.persist(playlist);
    }

    public Playlist findById(Long id) {
        return em.find(Playlist.class, id);
    }

    public List<Playlist> findByMember(Member member) {
        return em.createQuery("select p from Playlist as p where p.member = :member", Playlist.class)
                .setParameter("member", member)
                .getResultList();
    }
}
