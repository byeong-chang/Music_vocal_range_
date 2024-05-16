package capstone.tunemaker.repository;

import capstone.tunemaker.entity.Member;
import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.enums.Genre;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MusicRepository {

    private final EntityManager em;

    public void save(Music music) {
        em.persist(music);
    }

    // findOne으로 return em.find(Music.class, id);를 못하는 이유는 클라이언트가 넘겨주는 값(Url)이 정상인지 비정상인지 확인하기 전이라
    // 함부로 Music 테이블에 값을 저장하면서 id값을 확인할 수가 없음
    public Music findByUrlId(String urlId){
        List<Music> results = em.createQuery("select m from Music as m where m.urlId=:urlId", Music.class)
                .setParameter("urlId", urlId)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }


    public List<Music> findByTitleContaining(String keyword){
        return em.createQuery("select m from Music as m where m.title like :keyword", Music.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }


    public List<Music> find10ByGenre(Genre genre) {
        return em.createQuery("select m from Music as m where m.genre = :genre order by m.id", Music.class)
                .setParameter("genre", genre)
                .setMaxResults(10)
                .getResultList();
    }

    public List<Music> find10ByPitch(Double highPitch) {
        return em.createQuery("select m from Music as m where m.highPitch <= :highPitch", Music.class)
                .setParameter("highPitch", highPitch)
                .setMaxResults(10)
                .getResultList();
    }

}
