package pl.rotkom.friday.core.repository.service.youtube;

import org.springframework.stereotype.Service;

import pl.rotkom.friday.core.repository.common.ExternalEntityService;
import pl.rotkom.friday.core.repository.dao.youtube.IYouTubeChannelDao;
import pl.rotkom.friday.core.repository.model.youtube.YouTubeChannel;

@Service
class YouTubeChannelService extends ExternalEntityService<YouTubeChannel> implements IYouTubeChannelService {

    private final IYouTubeChannelDao dao;

    public YouTubeChannelService(IYouTubeChannelDao dao) {
        super(dao);
        this.dao = dao;
    }
}
