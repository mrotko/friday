package pl.rotkom.friday.core.repository.dao.youtube;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import pl.rotkom.friday.core.repository.common.ExternalEntityDao;
import pl.rotkom.friday.core.repository.model.youtube.YouTubeChannel;

@Repository
class YouTubeChannelDao extends ExternalEntityDao<YouTubeChannel> implements IYouTubeChannelDao {

    public YouTubeChannelDao(EntityManager entityManager) {
        super(YouTubeChannel.class, entityManager);
    }
}
