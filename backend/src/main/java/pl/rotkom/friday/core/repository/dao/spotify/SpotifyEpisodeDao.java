package pl.rotkom.friday.core.repository.dao.spotify;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import pl.rotkom.friday.core.repository.common.ExternalEntityDao;
import pl.rotkom.friday.core.repository.model.spotify.SpotifyEpisode;

@Repository
class SpotifyEpisodeDao extends ExternalEntityDao<SpotifyEpisode> implements ISpotifyEpisodeDao {

    public SpotifyEpisodeDao(EntityManager entityManager) {
        super(SpotifyEpisode.class, entityManager);
    }
}
