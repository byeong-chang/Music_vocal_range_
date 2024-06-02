package capstone.tunemaker.repository;

import capstone.tunemaker.entity.Music;
import capstone.tunemaker.entity.Playlist;
import capstone.tunemaker.entity.PlaylistAndMusic;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaylistAndMusicRepository {

    private final EntityManager em;

    public void save(PlaylistAndMusic playlistAndMusic) {
        em.persist(playlistAndMusic);
    }

    public void delete(PlaylistAndMusic playlistAndMusic) {
        em.remove(playlistAndMusic);
    }

    public PlaylistAndMusic findByPlaylistAndMusic(Playlist playlist, Music music) {
        List<PlaylistAndMusic> results = em.createQuery("select pm from PlaylistAndMusic pm where pm.playlist = :playlist and pm.music = :music", PlaylistAndMusic.class)
                .setParameter("playlist", playlist)
                .setParameter("music", music)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

}
