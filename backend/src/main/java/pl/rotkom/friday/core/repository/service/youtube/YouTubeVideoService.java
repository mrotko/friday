package pl.rotkom.friday.core.repository.service.youtube;

import org.springframework.stereotype.Service;

import pl.rotkom.friday.core.repository.common.ExternalEntityService;
import pl.rotkom.friday.core.repository.dao.spotify.ISpotifyShowDao;
import pl.rotkom.friday.core.repository.dao.youtube.IYouTubeVideoDao;
import pl.rotkom.friday.core.repository.model.youtube.YouTubeVideo;

@Service
class YouTubeVideoService extends ExternalEntityService<YouTubeVideo> implements IYouTubeVideoService {

    private final IYouTubeVideoDao dao;

    public YouTubeVideoService(IYouTubeVideoDao dao) {
        super(dao);
        this.dao = dao;
    }
}
