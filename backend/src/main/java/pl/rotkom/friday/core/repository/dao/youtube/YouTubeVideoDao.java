package pl.rotkom.friday.core.repository.dao.youtube;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import pl.rotkom.friday.core.repository.common.ExternalEntityDao;
import pl.rotkom.friday.core.repository.model.youtube.YouTubeVideo;

@Repository
class YouTubeVideoDao extends ExternalEntityDao<YouTubeVideo> implements IYouTubeVideoDao {

    public YouTubeVideoDao(EntityManager entityManager) {
        super(YouTubeVideo.class, entityManager);
    }
}
