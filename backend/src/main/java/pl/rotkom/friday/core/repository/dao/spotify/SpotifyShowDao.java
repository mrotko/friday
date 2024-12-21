package pl.rotkom.friday.core.repository.dao.spotify;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import pl.rotkom.friday.core.repository.common.ExternalEntityDao;
import pl.rotkom.friday.core.repository.model.spotify.SpotifyShow;

@Repository
class SpotifyShowDao extends ExternalEntityDao<SpotifyShow> implements ISpotifyShowDao {

    public SpotifyShowDao(EntityManager entityManager) {
        super(SpotifyShow.class, entityManager);
    }
}
